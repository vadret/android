package fi.kroon.vadret.domain.weatherforecastwidget.shared

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.functional.flatMap
import fi.kroon.vadret.data.functional.flatMapSingle
import fi.kroon.vadret.data.functional.map
import fi.kroon.vadret.data.location.model.Location
import fi.kroon.vadret.data.nominatim.model.NominatimReverseOut
import fi.kroon.vadret.data.weatherforecast.model.Weather
import fi.kroon.vadret.data.weatherforecast.model.WeatherOut
import fi.kroon.vadret.domain.IService
import fi.kroon.vadret.domain.weatherforecast.GetLocationAutomaticTask
import fi.kroon.vadret.domain.weatherforecast.GetReverseLocalityNameTask
import fi.kroon.vadret.domain.weatherforecast.GetWeatherForecastDiskCacheTask
import fi.kroon.vadret.domain.weatherforecast.GetWeatherForecastMemoryCacheTask
import fi.kroon.vadret.domain.weatherforecast.GetWeatherForecastTask
import fi.kroon.vadret.domain.weatherforecast.SetWeatherForecastDiskCacheTask
import fi.kroon.vadret.domain.weatherforecast.SetWeatherForecastMemoryCacheTask
import fi.kroon.vadret.util.FIVE_MINUTES_IN_MILLIS
import fi.kroon.vadret.util.extension.asSingle
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith
import javax.inject.Inject
import timber.log.Timber

class GetWidgetWeatherForecastService @Inject constructor(
    private val getWeatherForecastTask: GetWeatherForecastTask,
    private val getLocationAutomaticTask: GetLocationAutomaticTask,
    private val getReverseLocalityNameTask: GetReverseLocalityNameTask,
    private val getWidgetLocationModeKeyValueTask: GetWidgetLocationModeKeyValueTask,
    private val setWeatherForecastDiskCacheTask: SetWeatherForecastDiskCacheTask,
    private val setWeatherForecastMemoryCacheTask: SetWeatherForecastMemoryCacheTask,
    private val getWeatherForecastMemoryCacheTask: GetWeatherForecastMemoryCacheTask,
    private val getWeatherForecastDiskCacheTask: GetWeatherForecastDiskCacheTask,
    private val getWidgetLocationManualService: GetWidgetLocationManualService
) : IService {

    data class Data(
        val localityName: String? = null,
        val forceNet: Boolean = false,
        val weather: Weather? = null,
        val weatherOut: WeatherOut? = null,
        val timeStamp: Long,
        val locationMode: Boolean = false,
        val location: Location? = null,
        val appWidgetId: Int = 0
    )

    /**
     *  [cacheKey] Must conform to regex [a-z0-9_-]{1,120}
     */
    private companion object {
        const val cacheKey: String = "weather_forecast_widget_cache_key_"
    }

    /**
     *  [timeStamp]         -- Timestamp issued at time of request, used to control whether cache or network
     *  should be used.
     *  [forceNet]          -- Forces a network request regardless of value in timeStamp.
     */
    operator fun invoke(timeStamp: Long, forceNet: Boolean, appWidgetId: Int): Single<Either<Failure, Data>> =
        Single.just(Data(timeStamp = timeStamp, forceNet = forceNet, appWidgetId = appWidgetId))
            .flatMap(::getLocationMode)
            .flatMap(::getGpsLocationOrStoredLocation)
            .flatMap(::getWeatherForecastList)
            .flatMap(::doReverseNominatimLookupOrReturn)

    /**
     *  Determine if location should be derived from GPS or local storage.
     */
    private fun getLocationMode(data: Data): Single<Either<Failure, Data>> =
        getWidgetLocationModeKeyValueTask(appWidgetId = data.appWidgetId)
            .map { either: Either<Failure, Boolean> ->
                either.map { locationMode ->
                    Timber.d("WIDGET LOCATION MODE: $locationMode")
                    data.copy(locationMode = locationMode)
                }
            }

    private fun getGpsLocationOrStoredLocation(either: Either<Failure, Data>): Single<Either<Failure, Data>> =
        either.flatMapSingle { data: Data ->
            when (data.locationMode) {
                false -> getLocationManual(data)
                true -> getLocationAutomatic(data)
                    .map(::mapLocationEntityToWeatherOut)
            }
        }

    private fun getLocationAutomatic(data: Data): Single<Either<Failure, Data>> =
        getLocationAutomaticTask().map { either: Either<Failure, Location> ->
            either.map { location: Location ->
                data.copy(location = location)
            }
        }

    private fun getLocationManual(data: Data): Single<Either<Failure, Data>> =
        getWidgetLocationManualService(data.appWidgetId)
            .map { either: Either<Failure, WeatherOut> ->
                either.map { weatherOut: WeatherOut ->
                    Timber.d("WIDGET WEATHER DATA: $weatherOut")
                    data.copy(
                        weatherOut = weatherOut,
                        localityName = weatherOut.localityName!!
                    )
                }
            }

    /**
     *  Takes location and creates a weather forecast request object
     */
    private fun mapLocationEntityToWeatherOut(either: Either<Failure, Data>): Either<Failure, Data> =
        either.map { data: Data ->
            val weatherOut = WeatherOut(
                latitude = data.location!!.latitude,
                longitude = data.location.longitude
            )
            data.copy(weatherOut = weatherOut)
        }

    /**
     *  If the response data was not pulled from a cache store
     *  the [timeStamp] is updated to [currentTimeMillis]
     */
    private fun getWeatherForecastList(either: Either<Failure, Data>): Single<Either<Failure, Data>> =
        either.flatMapSingle { data: Data ->
            getWeather(data)
        }

    private fun doReverseNominatimLookupOrReturn(either: Either<Failure, Data>): Single<Either<Failure, Data>> =
        either.flatMapSingle { data: Data ->
            when (data.locationMode) {
                true -> doReverseNominatimLookup(either)
                false -> {
                    either.asSingle()
                }
            }
        }

    private fun doReverseNominatimLookup(either: Either<Failure, Data>): Single<Either<Failure, Data>> =
        either.flatMapSingle { data: Data ->
            val nominatimReverseOut = NominatimReverseOut(
                latitude = data.weatherOut!!.latitude,
                longitude = data.weatherOut.longitude
            )
            getReverseLocalityNameTask(nominatimReverseOut).map { result ->
                result.map { localityName: String? ->
                    localityName?.let {
                        data.copy(localityName = localityName)
                    } ?: data
                }
            }
        }

    private fun getWeather(data: Data): Single<Either<Failure, Data>> =
        with(data) {
            when {
                forceNet || (currentTimeMillis > (timeStamp + FIVE_MINUTES_IN_MILLIS)) -> {
                    Timber.d("NETWORK RESPONSE: $data")
                    getWeatherForecastTask(data.weatherOut!!)
                        .map { either: Either<Failure, Weather> ->
                            either.map { weather: Weather ->
                                data.copy(weather = weather, timeStamp = currentTimeMillis)
                            }
                        }.flatMap { either: Either<Failure, Data> ->
                            updateCache(either)
                        }
                }
                else -> {
                    /**
                     *  [cacheKey] + [appWidgetId] is always the cache key.
                     */
                    Timber.d("OFFLINE RESPONSE: $data")
                    Single.merge(
                        getWeatherForecastMemoryCacheTask(cacheKey + appWidgetId)
                            .map { either: Either<Failure, Weather> ->
                                either.map { weather ->
                                    data.copy(weather = weather)
                                }
                            },
                        getWeatherForecastDiskCacheTask(cacheKey + appWidgetId)
                            .map { either: Either<Failure, Weather> ->
                                either.map { weather ->
                                    data.copy(weather = weather)
                                }
                            }
                    ).filter { result: Either<Failure, Data> ->
                        result.either(
                            {
                                false
                            },
                            { data ->
                                Timber.d("CACHED RESPONSE: ${data.weather}")
                                data
                                    .weather!!
                                    .timeSeries!!
                                    .isNotEmpty()
                            }
                        )
                    }.take(1)
                        .switchIfEmpty(
                            getWeatherForecastTask(data.weatherOut!!)
                                .map { either: Either<Failure, Weather> ->
                                    Timber.d("CACHE EMPTY. NETWORK REQUEST")
                                    either.map { weather: Weather ->
                                        data.copy(weather = weather, timeStamp = currentTimeMillis)
                                    }
                                }.flatMap { data ->
                                    updateCache(data)
                                }.toFlowable()
                        ).singleOrError()
                }
            }
        }

    /**
     *  [cacheKey] + [appWidgetId] is always the cache key.
     */
    private fun updateCache(either: Either<Failure, Data>): Single<Either<Failure, Data>> =
        either.flatMapSingle { data: Data ->
            setWeatherForecastMemoryCacheTask(cacheKey + data.appWidgetId, data.weather!!)
                .zipWith(setWeatherForecastDiskCacheTask(cacheKey + data.appWidgetId, data.weather))
                .map { pair: Pair<Either<Failure, Weather>,
                    Either<Failure, Weather>> ->
                    Timber.i("Updating cache")
                    val (firstEither: Either<Failure, Weather>,
                        secondEither: Either<Failure, Weather>) = pair
                    firstEither.flatMap { _: Weather ->
                        secondEither.map { _: Weather ->
                            data
                        }
                    }
                }
        }
}