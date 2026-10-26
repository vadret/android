package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.weatherforecast.model.Analysis
import fi.kroon.vadret.weatherforecast.model.Forecast
import fi.kroon.vadret.weatherforecast.remote.DefaultWeatherForecastRemoteDataSource
import io.github.sphrak.either.Either
import io.ktor.client.HttpClient

interface WeatherForecastRepository {

    suspend fun getForecasts(
        latitude: Double,
        longitude: Double
    ): Either<Failure, List<Forecast>>

    suspend fun getAnalyses(
        latitude: Double,
        longitude: Double
    ): Either<Failure, List<Analysis>>
}

fun WeatherForecastRepository(httpClient: Lazy<HttpClient>): WeatherForecastRepository =
    DefaultWeatherForecastRepository(
        weatherForecastRemoteDataSource = DefaultWeatherForecastRemoteDataSource(httpClient = httpClient)
    )