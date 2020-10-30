package fi.kroon.vadret.domain.weatherforecast

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.location.model.Location
import fi.kroon.vadret.data.nominatim.model.NominatimReverseOut
import fi.kroon.vadret.data.weatherforecast.model.Weather
import fi.kroon.vadret.data.weatherforecast.model.WeatherOut
import fi.kroon.vadret.domain.IService
import fi.kroon.vadret.presentation.weatherforecast.WeatherForecastMapper
import fi.kroon.vadret.presentation.weatherforecast.model.IWeatherForecastModel
import io.github.sphrak.either.Either
import io.github.sphrak.either.flatMap
import io.github.sphrak.either.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetWeatherForecastService @Inject constructor(
    private val getWeatherForecastTask: GetWeatherForecastTask,
    private val getLocationAutomaticTask: GetLocationAutomaticTask,
    private val getReverseLocalityNameTask: GetReverseLocalityNameTask,
    private val getAppLocationModeTask: GetAppLocationModeTask,
    private val getLocationManualTask: GetLocationManualTask
) : IService {

    data class Data(
        val weatherForecastModelList: List<IWeatherForecastModel> = listOf(),
        val localityName: String? = null,
        val forceNet: Boolean = false,
        val weather: Weather? = null,
        val weatherOut: WeatherOut? = null,
        val timeStamp: Long,
        val locationMode: Boolean = false,
        val location: Location? = null
    )

    /**
     *  [timeStamp]         -- Timestamp issued at time of request, used to control whether cache or network
     *  should be used.
     *  [forceNet]          -- Forces a network request regardless of value in timeStamp.
     */
    suspend operator fun invoke(timeStamp: Long, forceNet: Boolean): Either<Failure, Data> =
        withContext(Dispatchers.Default) {

            val initial = Data(timeStamp = timeStamp, forceNet = forceNet)

            val a = getLocationMode(initial)
            val b = getGpsLocationOrStoredLocation(a)
            val c = getWeatherForecastList(b)
            val d = getWeatherForecastList(c)
            val e = doReverseNominatimLookupOrReturn(d)
            val f = transform(e)
            f
        }

    /**
     *  Determine if location should be derived from GPS or local storage.
     */
    private suspend fun getLocationMode(data: Data): Either<Failure, Data> =
        getAppLocationModeTask()
            .await()
            .map { locationMode: Boolean ->
                data.copy(locationMode = locationMode)
            }

    private suspend fun getGpsLocationOrStoredLocation(either: Either<Failure, Data>): Either<Failure, Data> =
        either.flatMap { data: Data ->
            when (data.locationMode) {
                false -> getLocationManual(data)
                true -> mapLocationEntityToWeatherOut(getLocationAutomatic(data))
            }
        }

    private suspend fun getLocationAutomatic(data: Data): Either<Failure, Data> =
        getLocationAutomaticTask()
            .await()
            .map { location: Location ->
                data.copy(location = location)
            }

    private suspend fun getLocationManual(data: Data): Either<Failure, Data> =
        getLocationManualTask()
            .await()
            .map { weatherOut: WeatherOut ->
                data.copy(
                    weatherOut = weatherOut,
                    localityName = weatherOut.localityName!!
                )
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

    private suspend fun getWeatherForecastList(either: Either<Failure, Data>): Either<Failure, Data> =
        either.flatMap { data: Data ->
            getWeather(data)
                .map { dataIn: Data ->
                    dataIn.copy(
                        timeStamp = currentTimeMillis
                    )
                }
        }

    private suspend fun doReverseNominatimLookupOrReturn(either: Either<Failure, Data>): Either<Failure, Data> =
        either.flatMap { data: Data ->
            when (data.locationMode) {
                true -> doReverseNominatimLookup(either)
                false -> {
                    either
                }
            }
        }

    private suspend fun doReverseNominatimLookup(either: Either<Failure, Data>): Either<Failure, Data> =
        either.flatMap { data: Data ->
            val nominatimReverseOut = NominatimReverseOut(
                latitude = data.weatherOut!!.latitude,
                longitude = data.weatherOut.longitude
            )
            getReverseLocalityNameTask(nominatimReverseOut)
                .await()
                .map { localityName: String? ->
                    localityName?.let {
                        data.copy(localityName = localityName)
                    } ?: data
                }
        }

    private suspend fun getWeather(data: Data): Either<Failure, Data> {
        return getWeatherForecastTask(data.weatherOut!!)
            .map { weather: Weather ->
                data.copy(weather = weather)
            }
    }

    private fun transform(either: Either<Failure, Data>): Either<Failure, Data> =
        either.map { data: Data ->
            val locationEntity = Location(data.weatherOut!!.latitude, data.weatherOut.longitude)
            val baseWeatherForecastModelList: List<IWeatherForecastModel> = WeatherForecastMapper(
                data.weather!!.timeSeries,
                locationEntity
            )
            data.copy(weatherForecastModelList = baseWeatherForecastModelList)
        }
}