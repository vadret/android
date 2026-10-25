package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.nominatim.model.Locality
import fi.kroon.vadret.weatherforecast.model.PlaceForecast
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asLeft
import io.github.sphrak.either.extension.asRight
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultGetWeatherForecastTaskTest {

    private val locationRepository = FakeLocationRepository()
    private val weatherForecastRepository = FakeWeatherForecastRepository()
    private val nominatimRepository = FakeNominatimRepository()
    private val getWeatherForecastTask: GetWeatherForecastTask = DefaultGetWeatherForecastTask(
        locationRepository = locationRepository,
        weatherForecastRepository = weatherForecastRepository,
        nominatimRepository = nominatimRepository
    )

    @Test
    fun `a stored place is forecast under its own name`() =
        runTest {
            assertEquals<Either<Failure, PlaceForecast>>(
                expected = PlaceForecast(
                    placeName = "Stockholm",
                    latitude = 59.3293,
                    longitude = 18.0686,
                    forecasts = listOf(noon)
                ).asRight(),
                actual = getWeatherForecastTask()
            )
        }

    @Test
    fun `the device location is forecast and named by OpenStreetMap`() =
        runTest {
            locationRepository.isAutomatic = true.asRight()

            assertEquals<Either<Failure, PlaceForecast>>(
                expected = PlaceForecast(
                    placeName = "Göteborg",
                    latitude = 57.7,
                    longitude = 11.97,
                    forecasts = listOf(noon)
                ).asRight(),
                actual = getWeatherForecastTask()
            )
            assertEquals(expected = listOf(57.7 to 11.97), actual = weatherForecastRepository.requestedCoordinates)
        }

    @Test
    fun `a place that cannot be named still gets its forecast`() =
        runTest {
            locationRepository.isAutomatic = true.asRight()
            val failures: List<Either<Failure, Locality>> = listOf(
                Failure(errorCode = ErrorCode.HTTP_FORBIDDEN).asLeft(),
                Locality(name = null).asRight()
            )

            val placeNames: List<String?> = failures.map { locality: Either<Failure, Locality> ->
                nominatimRepository.locality = locality
                (getWeatherForecastTask() as Either.Right).b.placeName
            }

            assertEquals(expected = listOf(null, null), actual = placeNames)
        }

    @Test
    fun `failures before the forecast stop it`() =
        runTest {
            val modeFailure = Failure(errorCode = ErrorCode.STORAGE_FAILED)
            val locationFailure = Failure(errorCode = ErrorCode.LOCATION_PROVIDER_DISABLED)
            val forecastFailure = Failure(errorCode = ErrorCode.WEATHER_FORECAST_NOT_AVAILABLE_FOR_LOCATION)

            locationRepository.isAutomatic = modeFailure.asLeft()
            val withoutMode: Either<Failure, PlaceForecast> = getWeatherForecastTask()
            locationRepository.isAutomatic = true.asRight()
            locationRepository.deviceLocation = locationFailure.asLeft()
            val withoutLocation: Either<Failure, PlaceForecast> = getWeatherForecastTask()
            locationRepository.isAutomatic = false.asRight()
            weatherForecastRepository.forecasts = forecastFailure.asLeft()
            val withoutForecast: Either<Failure, PlaceForecast> = getWeatherForecastTask()

            assertEquals(
                expected = listOf(modeFailure, locationFailure, forecastFailure).map { failure: Failure -> failure.asLeft() },
                actual = listOf(withoutMode, withoutLocation, withoutForecast)
            )
            assertTrue(actual = weatherForecastRepository.requestedCoordinates.size == 1)
        }
}