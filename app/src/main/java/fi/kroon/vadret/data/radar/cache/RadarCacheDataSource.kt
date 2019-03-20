package fi.kroon.vadret.data.radar.cache

import androidx.collection.LruCache
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.utils.RADAR_CACHE_KEY
import fi.kroon.vadret.utils.extensions.asLeft
import fi.kroon.vadret.utils.extensions.asRight
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class RadarCacheDataSource @Inject constructor(
    private val diskCache: RadarDiskCacheImpl,
    private val memoryCache: LruCache<Long, Radar>
) {
    fun getMemoryCache(): Single<Either<Failure, Radar>> =
        Single.fromCallable {
            memoryCache
                .snapshot()
                .getValue(RADAR_CACHE_KEY)
                .asRight() as Either<Failure, Radar>
        }.onErrorReturn {
            Failure
                .MemoryCacheLruReadFailure
                .asLeft()
        }

    fun getDiskCache(): Single<Either<Failure, Radar>> = diskCache
        .read()

    fun updateMemoryCache(radar: Radar): Single<Either<Failure, Radar>> {
        memoryCache.put(RADAR_CACHE_KEY, radar)
        return Single.just(
            radar.asRight() as Either<Failure, Radar>
        ).onErrorReturn {
            Failure
                .MemoryCacheLruWriteFailure
                .asLeft()
        }
    }

    fun updateDiskCache(radar: Radar): Single<Either<Failure, Radar>> =
        diskCache.put(radar)

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