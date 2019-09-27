package fi.kroon.vadret.domain.weatherforecast

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.weatherforecast.cache.WeatherForecastCacheDataSource
import fi.kroon.vadret.data.weatherforecast.model.Weather
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject
import timber.log.Timber

class SetWeatherForecastMemoryCacheTask @Inject constructor(
    private val cacheRepo: WeatherForecastCacheDataSource
) {
    operator fun invoke(cacheKey: String, weather: Weather): Single<Either<Failure, Weather>> =
        cacheRepo
            .updateMemoryCache(cacheKey, weather)
            .doOnError {
                Timber.e("$it")
            }
}