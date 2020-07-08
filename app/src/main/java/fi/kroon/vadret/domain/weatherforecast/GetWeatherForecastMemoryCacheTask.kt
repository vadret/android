package fi.kroon.vadret.domain.weatherforecast

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.weatherforecast.cache.WeatherForecastCacheDataSource
import fi.kroon.vadret.data.weatherforecast.model.Weather
import io.github.sphrak.either.Either
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class GetWeatherForecastMemoryCacheTask @Inject constructor(
    private val cacheRepo: WeatherForecastCacheDataSource
) {
    operator fun invoke(cacheKey: String): Single<Either<Failure, Weather>> =
        cacheRepo
            .getMemoryCache(cacheKey)
            .doOnError {
                Timber.e("$it")
            }
}