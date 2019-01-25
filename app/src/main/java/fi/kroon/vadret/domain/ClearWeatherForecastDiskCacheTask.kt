package fi.kroon.vadret.domain

import fi.kroon.vadret.data.cache.WeatherForecastCacheRepository
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class ClearWeatherForecastDiskCacheTask @Inject constructor(
    private val cacheRepo: WeatherForecastCacheRepository
) {
    operator fun invoke(): Single<Either<Failure, Unit>> =
        cacheRepo
            .clearDiskCache()
            .doOnError {
                Timber.e("DisplayError clearing disk cache: $it")
            }
}