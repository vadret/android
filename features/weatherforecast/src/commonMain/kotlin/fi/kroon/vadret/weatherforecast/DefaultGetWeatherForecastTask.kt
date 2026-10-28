package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.location.LocationRepository
import fi.kroon.vadret.location.model.Location
import fi.kroon.vadret.location.model.StoredLocation
import fi.kroon.vadret.nominatim.NominatimRepository
import fi.kroon.vadret.nominatim.model.Locality
import fi.kroon.vadret.weatherforecast.model.Analysis
import fi.kroon.vadret.weatherforecast.model.Forecast
import fi.kroon.vadret.weatherforecast.model.PlaceForecast
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asRight
import io.github.sphrak.either.extension.onResult
import io.github.sphrak.either.flatMap
import io.github.sphrak.either.map
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

internal class DefaultGetWeatherForecastTask(
    private val locationRepository: LocationRepository,
    private val weatherForecastRepository: WeatherForecastRepository,
    private val nominatimRepository: NominatimRepository
) : GetWeatherForecastTask {

    override suspend fun invoke(): Either<Failure, PlaceForecast> =
        locationRepository
            .isAutomatic()
            .flatMap { isAutomatic: Boolean ->
                if (isAutomatic) {
                    getDeviceLocationForecast()
                } else {
                    getStoredLocationForecast()
                }
            }

    private suspend fun getDeviceLocationForecast(): Either<Failure, PlaceForecast> =
        locationRepository
            .getDeviceLocation()
            .flatMap { location: Location ->
                getPlaceForecast(
                    latitude = location.latitude,
                    longitude = location.longitude
                ).flatMap { placeForecast: PlaceForecast ->
                    val locality: Locality? = getLocality(location = location)
                    placeForecast
                        .copy(
                            placeName = locality?.name,
                            municipality = locality?.municipality,
                            county = locality?.county
                        ).asRight()
                }
            }

    private suspend fun getStoredLocationForecast(): Either<Failure, PlaceForecast> =
        locationRepository
            .getStoredLocation()
            .flatMap { storedLocation: StoredLocation ->
                getPlaceForecast(
                    latitude = storedLocation.latitude,
                    longitude = storedLocation.longitude
                ).map { placeForecast: PlaceForecast ->
                    placeForecast.copy(
                        placeName = storedLocation.locality,
                        municipality = storedLocation.municipality,
                        county = storedLocation.county
                    )
                }
            }

    private suspend fun getPlaceForecast(
        latitude: Double,
        longitude: Double
    ): Either<Failure, PlaceForecast> =
        coroutineScope {
            val analyses: Deferred<List<Analysis>> = async {
                getAnalyses(
                    latitude = latitude,
                    longitude = longitude
                )
            }
            val forecasts: Either<Failure, List<Forecast>> = weatherForecastRepository.getForecasts(
                latitude = latitude,
                longitude = longitude
            )
            if (forecasts.isLeft) {
                analyses.cancel()
            }
            forecasts.flatMap { forecastList: List<Forecast> ->
                PlaceForecast(
                    placeName = null,
                    municipality = null,
                    county = null,
                    latitude = latitude,
                    longitude = longitude,
                    forecasts = forecastList,
                    analyses = analyses.await()
                ).asRight()
            }
        }

    private suspend fun getAnalyses(
        latitude: Double,
        longitude: Double
    ): List<Analysis> =
        weatherForecastRepository
            .getAnalyses(
                latitude = latitude,
                longitude = longitude
            ).onResult(
                onError = { _: Failure -> emptyList() },
                onSuccess = { analyses: List<Analysis> -> analyses }
            )

    private suspend fun getLocality(location: Location): Locality? =
        nominatimRepository
            .getLocality(
                latitude = location.latitude,
                longitude = location.longitude
            ).onResult(
                onError = { _: Failure -> null },
                onSuccess = { locality: Locality -> locality }
            )
}