package fi.kroon.vadret.domain.weatherforecast

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.weatherforecast.cache.WeatherForecastCacheDataSource
import io.github.sphrak.either.Either
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class ClearWeatherForecastMemoryCacheTask @Inject constructor(
    private val cacheRepo: WeatherForecastCacheDataSource
) {
    operator fun invoke(): Single<Either<Failure, Unit>> =
        cacheRepo
            .clearMemoryCache()
            .doOnError {
                Timber.e("DisplayError clearing memory cache: $it")
            }
}