package fi.kroon.vadret.domain.weatherforecast

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.weatherforecast.WeatherForecastRepository
import fi.kroon.vadret.data.weatherforecast.model.Weather
import fi.kroon.vadret.data.weatherforecast.model.WeatherOut
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class GetWeatherForecastTask @Inject constructor(
    private val repo: WeatherForecastRepository
) {
    operator fun invoke(request: WeatherOut): Single<Either<Failure, Weather>> =
        repo.get(
            request
        ).doOnError {
            Timber.e("$it")
        }
}