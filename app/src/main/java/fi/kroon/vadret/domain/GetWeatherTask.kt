package fi.kroon.vadret.domain

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.weather.WeatherForecastRepository
import fi.kroon.vadret.data.weather.model.Weather
import fi.kroon.vadret.data.weather.model.WeatherOut
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class GetWeatherTask @Inject constructor(
    private val weatherForecastRepository: WeatherForecastRepository
) {

    operator fun invoke(
        request: WeatherOut
    ): Single<Either<Failure, Weather>> =
        weatherForecastRepository.get(
            request
        ).doOnError {
            Timber.e("$it")
        }
}