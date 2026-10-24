package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.location.LocationRepository
import fi.kroon.vadret.location.model.Location
import fi.kroon.vadret.location.model.StoredLocation
import fi.kroon.vadret.nominatim.NominatimRepository
import fi.kroon.vadret.nominatim.model.Locality
import fi.kroon.vadret.weatherforecast.model.Forecast
import fi.kroon.vadret.weatherforecast.model.PlaceForecast
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asRight
import io.github.sphrak.either.extension.onResult
import io.github.sphrak.either.flatMap
import io.github.sphrak.either.map
import javax.inject.Inject

internal class DefaultGetWeatherForecastTask @Inject constructor(
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
                getForecasts(
                    latitude = location.latitude,
                    longitude = location.longitude
                ).flatMap { forecasts: List<Forecast> ->
                    PlaceForecast(
                        placeName = getPlaceName(location = location),
                        latitude = location.latitude,
                        longitude = location.longitude,
                        forecasts = forecasts
                    ).asRight()
                }
            }

    private suspend fun getStoredLocationForecast(): Either<Failure, PlaceForecast> =
        locationRepository
            .getStoredLocation()
            .flatMap { storedLocation: StoredLocation ->
                getForecasts(
                    latitude = storedLocation.latitude,
                    longitude = storedLocation.longitude
                ).map { forecasts: List<Forecast> ->
                    PlaceForecast(
                        placeName = storedLocation.locality,
                        latitude = storedLocation.latitude,
                        longitude = storedLocation.longitude,
                        forecasts = forecasts
                    )
                }
            }

    private suspend fun getForecasts(
        latitude: Double,
        longitude: Double
    ): Either<Failure, List<Forecast>> =
        weatherForecastRepository.getForecasts(
            latitude = latitude,
            longitude = longitude
        )

    /**
     *  The forecast is shown without a place name rather
     *  than not at all when the place cannot be named.
     */
    private suspend fun getPlaceName(location: Location): String? =
        nominatimRepository
            .getLocality(
                latitude = location.latitude,
                longitude = location.longitude
            ).onResult(
                onError = { _: Failure -> null },
                onSuccess = { locality: Locality -> locality.name }
            )
}