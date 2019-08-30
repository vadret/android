package fi.kroon.vadret.data.aggregatedfeed.cache

import androidx.collection.LruCache
import fi.kroon.vadret.data.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.util.AGGREGATED_FEED_CACHE_KEY
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject
import timber.log.Timber

class AggregatedFeedCacheDataSource @Inject constructor(
    private val diskCache: AggregatedFeedDiskCache,
    private val memoryCache: LruCache<Long, List<AggregatedFeed>>
) {

    fun getMemoryCache(): Single<Either<Failure, List<AggregatedFeed>>> =
        Single.fromCallable {
            memoryCache
                .snapshot()
                .getValue(AGGREGATED_FEED_CACHE_KEY)
                .asRight() as Either<Failure, List<AggregatedFeed>>
        }.onErrorReturn {
            Failure
                .MemoryCacheLruReadFailure
                .asLeft()
        }

    fun getDiskCache(): Single<Either<Failure, List<AggregatedFeed>>> =
        diskCache
            .read()

    fun updateMemoryCache(aggregatedFeedList: List<AggregatedFeed>): Single<Either<Failure, List<AggregatedFeed>>> {
        memoryCache.put(AGGREGATED_FEED_CACHE_KEY, aggregatedFeedList)
        return Single.just(
            aggregatedFeedList.asRight() as Either<Failure, List<AggregatedFeed>>
        ).onErrorReturn {
            Failure
                .MemoryCacheLruWriteFailure
                .asLeft()
        }
    }

    fun updateDiskCache(aggregatedFeedList: List<AggregatedFeed>): Single<Either<Failure, List<AggregatedFeed>>> =
        diskCache
            .put(aggregatedFeedList)

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