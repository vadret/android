package fi.kroon.vadret.domain.weatherforecast

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.weatherforecast.WeatherForecastRepository
import fi.kroon.vadret.data.weatherforecast.model.Weather
import fi.kroon.vadret.data.weatherforecast.model.WeatherOut
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject
import timber.log.Timber

class GetWeatherForecastTask @Inject constructor(
    private val repo: WeatherForecastRepository
) {
    operator fun invoke(request: WeatherOut): Single<Either<Failure, Weather>> =
        repo(request)
}