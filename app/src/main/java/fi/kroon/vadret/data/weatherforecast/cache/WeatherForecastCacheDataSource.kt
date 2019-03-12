package fi.kroon.vadret.data.weatherforecast.cache

import androidx.collection.LruCache
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.weatherforecast.model.Weather
import fi.kroon.vadret.utils.DEFAULT_WEATHER_CACHE_KEY
import fi.kroon.vadret.utils.extensions.asLeft
import fi.kroon.vadret.utils.extensions.asRight
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class WeatherForecastCacheDataSource @Inject constructor(
    private val diskCache: WeatherForecastDiskCache,
    private val memoryCache: LruCache<Long, Weather>
) {

    fun getMemoryCache(): Single<Either<Failure, Weather>> =
        Single.fromCallable {
            memoryCache
                .snapshot()
                .getValue(DEFAULT_WEATHER_CACHE_KEY)
                .asRight() as Either<Failure, Weather>
        }.onErrorReturn {
            Failure
                .MemoryCacheLruReadFailure
                .asLeft()
        }

    fun getDiskCache(): Single<Either<Failure, Weather>> = diskCache
        .read()

    fun updateMemoryCache(weather: Weather): Single<Either<Failure, Weather>> {
        memoryCache.put(DEFAULT_WEATHER_CACHE_KEY, weather)
        return Single.just(
            weather.asRight() as Either<Failure, Weather>
        ).onErrorReturn {
            Failure
                .MemoryCacheLruWriteFailure
                .asLeft()
        }
    }

    fun updateDiskCache(weather: Weather): Single<Either<Failure, Weather>> =
        diskCache.put(weather)

    fun clearMemoryCache(): Single<Either<Failure, Unit>> = Single.fromCallable {
        memoryCache
            .evictAll()
        Unit.asRight() as Either<Failure, Unit>
    }.doOnError { failure ->
        Timber.e("Memory cache eviction failed: $failure")
    }.onErrorReturn {
        Failure
            .MemoryCacheEvictionFailure
            .asLeft()
    }

    fun clearDiskCache(): Single<Either<Failure, Unit>> = Single.fromCallable {
        diskCache.remove()
        Unit.asRight() as Either<Failure, Unit>
    }.doOnError { failure ->
        Timber.e("Disk cache eviction failed: $failure")
    }.onErrorReturn {
        Failure
            .DiskCacheEvictionFailure
            .asLeft()
    }
}