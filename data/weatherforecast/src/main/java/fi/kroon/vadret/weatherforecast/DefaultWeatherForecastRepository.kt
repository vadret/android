package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.common.catch
import fi.kroon.vadret.common.either
import fi.kroon.vadret.weatherforecast.extension.toFailure
import fi.kroon.vadret.weatherforecast.extension.toForecast
import fi.kroon.vadret.weatherforecast.model.Forecast
import fi.kroon.vadret.weatherforecast.remote.TimeSeriesDto
import fi.kroon.vadret.weatherforecast.remote.WeatherForecastRemoteDataSource
import io.github.sphrak.either.Either

internal class DefaultWeatherForecastRepository(
    private val weatherForecastRemoteDataSource: WeatherForecastRemoteDataSource
) : WeatherForecastRepository {

    override suspend fun getForecasts(
        latitude: Double,
        longitude: Double
    ): Either<Failure, List<Forecast>> =
        either {
            weatherForecastRemoteDataSource
                .getWeatherForecast(
                    latitude = latitude,
                    longitude = longitude
                )
                .timeSeries
                .map { timeSeriesDto: TimeSeriesDto -> timeSeriesDto.toForecast() }
                .sortedBy { forecast: Forecast -> forecast.validAt }
        }.catch { exception: Exception -> exception.toFailure() }
}