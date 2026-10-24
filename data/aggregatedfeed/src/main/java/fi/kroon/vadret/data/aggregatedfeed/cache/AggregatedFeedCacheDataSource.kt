package fi.kroon.vadret.data.aggregatedfeed.cache

import fi.kroon.vadret.data.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.data.common.MemoryCache
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.util.AGGREGATED_FEED_CACHE_KEY
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import timber.log.Timber
import javax.inject.Inject

class AggregatedFeedCacheDataSource @Inject constructor(
    private val diskCache: AggregatedFeedDiskCache,
    private val memoryCache: MemoryCache<Long, List<AggregatedFeed>>
) {

    fun getMemoryCache(): Either<Failure, List<AggregatedFeed>> =
        memoryCache
            .get(AGGREGATED_FEED_CACHE_KEY)
            ?.asRight()
            ?: Failure
                .MemoryCacheLruReadFailure
                .asLeft()

    suspend fun getDiskCache(): Either<Failure, List<AggregatedFeed>> =
        diskCache
            .read()

    fun updateMemoryCache(aggregatedFeedList: List<AggregatedFeed>): Either<Failure, List<AggregatedFeed>> {
        memoryCache.put(AGGREGATED_FEED_CACHE_KEY, aggregatedFeedList)
        return aggregatedFeedList.asRight()
    }

    suspend fun updateDiskCache(aggregatedFeedList: List<AggregatedFeed>): Either<Failure, List<AggregatedFeed>> =
        diskCache
            .put(aggregatedFeedList)

    fun clearMemoryCache(): Either<Failure, Unit> {
        memoryCache
            .evictAll()
        return Unit.asRight()
    }

    suspend fun clearDiskCache(): Either<Failure, Unit> =
        when (diskCache.remove()) {
            is Either.Right -> Unit.asRight()
            is Either.Left -> {
                Timber.e("Disk cache eviction failed")
                Failure
                    .DiskCacheEvictionFailure
                    .asLeft()
            }
        }
}