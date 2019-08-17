package fi.kroon.vadret.domain.weatherforecast

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.weatherforecast.cache.WeatherForecastCacheDataSource
import io.reactivex.Single
import javax.inject.Inject
import timber.log.Timber

class ClearWeatherForecastDiskCacheTask @Inject constructor(
    private val cacheRepo: WeatherForecastCacheDataSource
) {
    operator fun invoke(cacheKey: String): Single<Either<Failure, Unit>> =
        cacheRepo
            .clearDiskCache(cacheKey)
            .doOnError {
                Timber.e("DisplayError clearing disk cache: $it")
            }
}