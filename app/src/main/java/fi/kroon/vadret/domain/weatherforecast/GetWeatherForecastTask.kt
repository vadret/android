package fi.kroon.vadret.domain.weatherforecast

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.weatherforecast.WeatherForecastRepository
import fi.kroon.vadret.data.weatherforecast.model.Weather
import fi.kroon.vadret.data.weatherforecast.model.WeatherOut
import io.github.sphrak.either.Either
import javax.inject.Inject

class GetWeatherForecastTask @Inject constructor(
    private val weatherForecastRepository: WeatherForecastRepository
) {
    suspend operator fun invoke(request: WeatherOut): Either<Failure, Weather> =
        weatherForecastRepository(request = request)
}