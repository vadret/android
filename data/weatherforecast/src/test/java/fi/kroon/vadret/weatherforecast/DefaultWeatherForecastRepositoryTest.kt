package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.logging.LogLevel
import fi.kroon.vadret.testing.LogLine
import fi.kroon.vadret.testing.mockHttpClient
import fi.kroon.vadret.testing.withRecordedLogs
import fi.kroon.vadret.weatherforecast.model.Forecast
import fi.kroon.vadret.weatherforecast.remote.DefaultWeatherForecastRemoteDataSource
import io.github.sphrak.either.Either
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")

private fun timeSeries(
    time: String,
    airTemperature: Double
): String =
    """
    {
      "time": "$time",
      "intervalParametersStartTime": "$time",
      "data": {
        "air_temperature": $airTemperature,
        "wind_from_direction": 225.0,
        "wind_speed": 4.2,
        "relative_humidity": 81,
        "thunderstorm_probability": 0,
        "predominant_precipitation_type_at_surface": 1,
        "probability_of_precipitation": 40,
        "precipitation_amount_min": 0.0,
        "precipitation_amount_max": 0.4,
        "symbol_code": 18
      }
    }
    """.trimIndent()

class DefaultWeatherForecastRepositoryTest {

    private val requests = mutableListOf<HttpRequestData>()

    private fun repository(handler: MockRequestHandler): WeatherForecastRepository =
        DefaultWeatherForecastRepository(
            weatherForecastRemoteDataSource = DefaultWeatherForecastRemoteDataSource(
                httpClient = lazy {
                    mockHttpClient { request: HttpRequestData ->
                        requests.add(request)
                        handler(request)
                    }
                }
            )
        )

    @Test
    fun `the forecast is asked for at six decimals at most`() = runTest {
        repository { respond(content = """{"timeSeries":[]}""", headers = jsonHeaders) }
            .getForecasts(
                latitude = 59.32938123,
                longitude = 18.0686
            )

        assertEquals(
            expected = "https://opendata-download-metfcst.smhi.se/api/category/snow1g/version/1/geotype/point/lon/18.0686/lat/59.329381/data.json",
            actual = requests.single().url.toString()
        )
    }

    @Test
    fun `the time series become forecasts in time order`() = runTest {
        val body = """
            {
              "createdTime": "2026-10-24T10:00:00Z",
              "timeSeries": [
                ${timeSeries(time = "2026-10-24T12:00:00Z", airTemperature = 8.5)},
                ${timeSeries(time = "2026-10-24T11:00:00Z", airTemperature = 7.0)}
              ]
            }
        """.trimIndent()

        val result: Either<Failure, List<Forecast>> = repository { respond(content = body, headers = jsonHeaders) }
            .getForecasts(
                latitude = 59.3293,
                longitude = 18.0686
            )

        assertEquals<Either<Failure, List<Forecast>>>(
            expected = Either.Right(
                b = listOf(
                    Forecast(
                        validAt = Instant.parse(input = "2026-10-24T11:00:00Z"),
                        airTemperature = 7.0,
                        windFromDirection = 225.0,
                        windSpeed = 4.2,
                        relativeHumidity = 81,
                        precipitationType = 1,
                        symbolCode = 18
                    ),
                    Forecast(
                        validAt = Instant.parse(input = "2026-10-24T12:00:00Z"),
                        airTemperature = 8.5,
                        windFromDirection = 225.0,
                        windSpeed = 4.2,
                        relativeHumidity = 81,
                        precipitationType = 1,
                        symbolCode = 18
                    )
                )
            ),
            actual = result
        )
    }

    @Test
    fun `a place outside the forecast area is told apart from other failures`() = runTest {
        withRecordedLogs { recordingLogSink ->
            val results: List<Either<Failure, List<Forecast>>> = listOf(
                repository { respond(content = "", status = HttpStatusCode.NotFound) },
                repository { respond(content = "", status = HttpStatusCode.ServiceUnavailable) },
                repository { respond(content = """{"timeSeries":[${timeSeries(time = "noon", airTemperature = 1.0)}]}""", headers = jsonHeaders) },
                repository { respond(content = """{"unexpected":true}""", headers = jsonHeaders) }
            ).map { weatherForecastRepository: WeatherForecastRepository ->
                weatherForecastRepository.getForecasts(
                    latitude = 70.0,
                    longitude = 40.0
                )
            }

            assertEquals(
                expected = listOf(
                    ErrorCode.WEATHER_FORECAST_NOT_AVAILABLE_FOR_LOCATION,
                    ErrorCode.HTTP_SERVICE_UNAVAILABLE,
                    ErrorCode.INVALID_RESPONSE,
                    ErrorCode.INVALID_RESPONSE
                ).map { errorCode: ErrorCode -> Either.Left(a = Failure(errorCode = errorCode)) },
                actual = results
            )
            assertEquals(
                expected = List(size = 4) { LogLevel.WARN },
                actual = recordingLogSink.lines.map { line: LogLine -> line.level }
            )
        }
    }
}