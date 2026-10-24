package fi.kroon.vadret.data.radar.cache

import fi.kroon.vadret.data.common.BaseCache
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.internal.cache.DiskLruCache
import okio.buffer
import timber.log.Timber

class RadarDiskCache(
    private val cache: DiskLruCache
) : BaseCache() {

    init {
        cache.initialize()
    }

    private companion object {
        const val KEY = "radar"
        const val INDEX = 0
    }

    suspend fun put(radar: Radar): Either<Failure, Radar> =
        withContext(Dispatchers.IO) {
            try {
                val editor = cache.edit(KEY)
                val sink = editor?.newSink(INDEX)
                val bufferedSink = sink?.buffer()
                val byteArray = serializerObject(radar)

                bufferedSink.use { _ ->
                    bufferedSink?.write(byteArray)
                }
                editor?.commit()

                radar.asRight()
            } catch (exception: Exception) {
                Timber.e("Disk cache insert failed: $exception")
                Failure
                    .DiskCacheLruWriteFailure
                    .asLeft()
            }
        }

    suspend fun read(): Either<Failure, Radar> =
        withContext(Dispatchers.IO) {
            try {
                val snapshot: DiskLruCache.Snapshot = cache[KEY]!!
                val byteArray: ByteArray = snapshot.getSource(INDEX)
                    .buffer()
                    .readByteArray()

                deserializeBytes<Radar>(byteArray)
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