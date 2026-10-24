package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.weatherforecast.model.Forecast
import fi.kroon.vadret.weatherforecast.remote.DefaultWeatherForecastRemoteDataSource
import io.github.sphrak.either.Either
import io.ktor.client.HttpClient

interface WeatherForecastRepository {

    /**
     *  SMHI's forecast for the next ten days, earliest first.
     */
    suspend fun getForecasts(
        latitude: Double,
        longitude: Double
    ): Either<Failure, List<Forecast>>
}

/**
 *  [httpClient] is only created on first use, never on the main thread.
 */
fun WeatherForecastRepository(httpClient: Lazy<HttpClient>): WeatherForecastRepository =
    DefaultWeatherForecastRepository(
        weatherForecastRemoteDataSource = DefaultWeatherForecastRemoteDataSource(httpClient = httpClient)
    )