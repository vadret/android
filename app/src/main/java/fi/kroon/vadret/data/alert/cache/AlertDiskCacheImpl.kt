package fi.kroon.vadret.data.alert.cache

import fi.kroon.vadret.data.alert.model.Alert
import fi.kroon.vadret.data.common.BaseCacheImpl
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.presentation.alert.di.AlertFeatureScope
import fi.kroon.vadret.utils.extensions.asLeft
import fi.kroon.vadret.utils.extensions.asRight
import io.reactivex.Single
import okhttp3.internal.cache.DiskLruCache
import okio.buffer
import timber.log.Timber
import javax.inject.Inject

@AlertFeatureScope
class AlertDiskCacheImpl @Inject constructor(
    private val cache: DiskLruCache
) : BaseCacheImpl() {

    init {
        cache.initialize()
    }

    private companion object {
        const val KEY = "alert"
        const val INDEX = 0
    }

    fun put(alert: Alert): Single<Either<Failure, Alert>> =
        Single.fromCallable {
            val editor = cache.edit(KEY)
            val sink = editor?.newSink(INDEX)
            val bufferedSink = sink?.buffer()
            val byteArray = serializerObject(alert)

            bufferedSink.use { _ ->
                bufferedSink?.write(byteArray)
            }
            editor?.commit()

            alert.asRight() as Either<Failure, Alert>
        }.doOnError {
            Timber.e("Disk cache put failed: $it")
        }.onErrorReturn {
            Failure
                .DiskCacheLruWriteFailure
                .asLeft()
        }

    fun read(): Single<Either<Failure, Alert>> = Single.fromCallable {
        val snapshot: DiskLruCache.Snapshot = cache.get(KEY)
        val byteArray: ByteArray
        byteArray = snapshot.getSource(INDEX)
            .buffer()
            .readByteArray()

        deserializeBytes<Alert>(byteArray)
            .asRight() as Either<Failure, Alert>
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