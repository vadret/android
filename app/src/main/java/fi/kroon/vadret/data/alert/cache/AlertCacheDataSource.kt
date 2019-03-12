package fi.kroon.vadret.data.alert.cache

import androidx.collection.LruCache
import fi.kroon.vadret.data.alert.model.Alert
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.utils.DEFAULT_ALERT_CACHE_KEY
import fi.kroon.vadret.utils.extensions.asLeft
import fi.kroon.vadret.utils.extensions.asRight
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class AlertCacheDataSource @Inject constructor(
    private val diskCache: AlertDiskCache,
    private val memoryCache: LruCache<Long, Alert>
) {

    fun getMemoryCache(): Single<Either<Failure, Alert>> =
        Single.fromCallable {
            memoryCache
                .snapshot()
                .getValue(DEFAULT_ALERT_CACHE_KEY)
                .asRight() as Either<Failure, Alert>
        }.onErrorReturn {
            Failure
                .MemoryCacheLruReadFailure
                .asLeft()
        }

    fun getDiskCache(): Single<Either<Failure, Alert>> =
        diskCache
            .read()

    fun updateMemoryCache(alert: Alert): Single<Either<Failure, Alert>> {
        memoryCache.put(DEFAULT_ALERT_CACHE_KEY, alert)
        return Single.just(
            alert.asRight() as Either<Failure, Alert>
        ).onErrorReturn {
            Failure
                .MemoryCacheLruWriteFailure
                .asLeft()
        }
    }

    fun updateDiskCache(alert: Alert): Single<Either<Failure, Alert>> =
        diskCache
            .put(alert)

    fun clearMemoryCache(): Single<Either<Failure, Unit>> = Single.fromCallable {
        memoryCache
            .evictAll()
        Unit.asRight() as Either<Failure, Unit>
    }.doOnError { failure ->
        Timber.e("Memory cache eviction failed: $failure")
    }.onErrorReturn {
        Failure
            .DiskCacheEvictionFailure
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