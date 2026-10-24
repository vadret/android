package fi.kroon.vadret.data.aggregatedfeed.cache

import fi.kroon.vadret.data.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.data.common.BaseCache
import fi.kroon.vadret.data.common.DiskCache
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class AggregatedFeedDiskCache(
    private val cache: DiskCache
) : BaseCache() {

    private companion object {
        const val KEY = "aggregatedfeed"
    }

    suspend fun put(aggregatedFeedList: List<AggregatedFeed>): Either<Failure, List<AggregatedFeed>> =
        withContext(Dispatchers.IO) {
            try {
                cache.write(KEY, serializerObject(aggregatedFeedList))
                aggregatedFeedList.asRight()
            } catch (exception: Exception) {
                Timber.e("Disk cache insert failed: $exception")
                Failure
                    .DiskCacheLruWriteFailure
                    .asLeft()
            }
        }

    suspend fun read(): Either<Failure, List<AggregatedFeed>> =
        withContext(Dispatchers.IO) {
            try {
                deserializeBytes<List<AggregatedFeed>>(cache.read(KEY)!!)
                    .asRight()
            } catch (exception: Exception) {
                Failure
                    .DiskCacheLruReadFailure
                    .asLeft()
            }
        }

    suspend fun remove(): Either<Failure, Boolean> =
        withContext(Dispatchers.IO) {
            try {
                cache.remove(KEY).asRight()
            } catch (exception: Exception) {
                Failure
                    .DiskCacheLruWriteFailure
                    .asLeft()
            }
        }
}