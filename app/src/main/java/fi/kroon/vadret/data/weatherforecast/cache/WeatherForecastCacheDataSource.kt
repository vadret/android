package fi.kroon.vadret.data.weatherforecast.cache

import androidx.collection.LruCache
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.weatherforecast.model.Weather
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject
import timber.log.Timber

class WeatherForecastCacheDataSource @Inject constructor(
    private val diskCache: WeatherForecastDiskCache,
    private val memoryCache: LruCache<String, Weather>
) {

    /**
     *  In-memory Cache
     */
    fun getMemoryCache(cacheKey: String): Single<Either<Failure, Weather>> =
        Single.fromCallable {
            memoryCache
                .snapshot()
                .getValue(cacheKey)
                .asRight() as Either<Failure, Weather>
        }.onErrorReturn {
            Failure
                .MemoryCacheLruReadFailure
                .asLeft()
        }

    fun updateMemoryCache(cacheKey: String, weather: Weather): Single<Either<Failure, Weather>> {
        memoryCache.put(cacheKey, weather)
        return Single.just(
            weather.asRight() as Either<Failure, Weather>
        ).onErrorReturn {
            Failure
                .MemoryCacheLruWriteFailure
                .asLeft()
        }
    }

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

    /**
     *  Disk IO Cache
     */
    fun getDiskCache(cacheKey: String): Single<Either<Failure, Weather>> = diskCache
        .read(cacheKey)

    fun updateDiskCache(weather: Weather, cacheKey: String): Single<Either<Failure, Weather>> =
        diskCache.put(cacheKey, weather)

    fun clearDiskCache(cacheKey: String): Single<Either<Failure, Unit>> = Single.fromCallable {
        diskCache.remove(cacheKey)
        Unit.asRight() as Either<Failure, Unit>
    }.doOnError { failure ->
        Timber.e("Disk cache eviction failed: $failure")
    }.onErrorReturn {
        Failure
            .DiskCacheEvictionFailure
            .asLeft()
    }
}