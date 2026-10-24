package fi.kroon.vadret.data.aggregatedfeed.cache

import fi.kroon.vadret.data.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.data.common.BaseCache
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.internal.cache.DiskLruCache
import okio.buffer
import timber.log.Timber

class AggregatedFeedDiskCache(
    private val cache: DiskLruCache
) : BaseCache() {

    init {
        cache.initialize()
    }

    private companion object {
        const val KEY = "aggregatedfeed"
        const val INDEX = 0
    }

    suspend fun put(aggregatedFeedList: List<AggregatedFeed>): Either<Failure, List<AggregatedFeed>> =
        withContext(Dispatchers.IO) {
            try {
                val editor = cache.edit(KEY)
                val sink = editor?.newSink(INDEX)
                val bufferedSink = sink?.buffer()
                val byteArray = serializerObject(aggregatedFeedList)

                bufferedSink.use { _ ->
                    bufferedSink?.write(byteArray)
                }
                editor?.commit()

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
                val snapshot: DiskLruCache.Snapshot = cache[KEY]!!
                val byteArray: ByteArray = snapshot.getSource(INDEX)
                    .buffer()
                    .readByteArray()

                deserializeBytes<List<AggregatedFeed>>(byteArray)
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