package fi.kroon.vadret.domain.weatherforecast

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.weatherforecast.cache.WeatherForecastCacheDataSource
import fi.kroon.vadret.data.weatherforecast.model.Weather
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class SetWeatherForecastDiskCacheTask @Inject constructor(
    private val repo: WeatherForecastCacheDataSource
) {
    operator fun invoke(cacheKey: String, weather: Weather): Single<Either<Failure, Weather>> =
        repo
            .updateDiskCache(weather, cacheKey)
            .doOnError {
                Timber.e("$it")
            }
}