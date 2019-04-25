package fi.kroon.vadret.domain.weatherforecast

import fi.kroon.vadret.data.weatherforecast.cache.WeatherForecastCacheDataSource
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

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