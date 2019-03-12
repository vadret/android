package fi.kroon.vadret.domain.weatherforecast

import fi.kroon.vadret.data.weatherforecast.cache.WeatherForecastCacheDataSource
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.weatherforecast.model.Weather
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class GetWeatherForecastMemoryCacheTask @Inject constructor(
    private val cacheRepo: WeatherForecastCacheDataSource
) {
    operator fun invoke(): Single<Either<Failure, Weather>> =
        cacheRepo
            .getMemoryCache()
            .doOnError {
                Timber.e("$it")
            }
}