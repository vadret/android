package fi.kroon.vadret.data.aggregatedfeed.cache

import fi.kroon.vadret.data.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.data.common.BaseCache
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.presentation.warning.display.di.WarningScope
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject
import okhttp3.internal.cache.DiskLruCache
import okio.buffer
import timber.log.Timber

@WarningScope
class AggregatedFeedDiskCache @Inject constructor(
    private val cache: DiskLruCache
) : BaseCache() {

    init {
        cache.initialize()
    }

    private companion object {
        const val KEY = "aggregatedfeed"
        const val INDEX = 0
    }

    fun put(aggregatedFeedList: List<AggregatedFeed>): Single<Either<Failure, List<AggregatedFeed>>> =
        Single.fromCallable {
            val editor = cache.edit(KEY)
            val sink = editor?.newSink(INDEX)
            val bufferedSink = sink?.buffer()
            val byteArray = serializerObject(aggregatedFeedList)

            bufferedSink.use { _ ->
                bufferedSink?.write(byteArray)
            }
            editor?.commit()

            aggregatedFeedList.asRight() as Either<Failure, List<AggregatedFeed>>
        }.doOnError {
            Timber.e("Disk cache insert failed: $it")
        }.onErrorReturn {
            Failure
                .DiskCacheLruWriteFailure
                .asLeft()
        }

    fun read(): Single<Either<Failure, List<AggregatedFeed>>> = Single.fromCallable {
        val snapshot: DiskLruCache.Snapshot = cache[KEY]!!
        val byteArray: ByteArray
        byteArray = snapshot.getSource(INDEX)
            .buffer()
            .readByteArray()

        deserializeBytes<List<AggregatedFeed>>(byteArray)
            .asRight() as Either<Failure, List<AggregatedFeed>>
    }.onErrorReturn {
        Failure
            .DiskCacheLruReadFailure
            .asLeft()
    }

    fun remove(): Single<Either<Failure, Boolean>> = Single.fromCallable {
        cache.remove(KEY).asRight() as Either<Failure, Boolean>
    }.onErrorReturn {
        Failure
            .DiskCacheLruWriteFailure
            .asLeft()
    }
}