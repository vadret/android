package fi.kroon.vadret.data.radar.cache

import fi.kroon.vadret.data.common.BaseCache
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.presentation.radar.di.RadarFeatureScope
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject
import okhttp3.internal.cache.DiskLruCache
import okio.buffer
import timber.log.Timber

@RadarFeatureScope
class RadarDiskCache @Inject constructor(
    private val cache: DiskLruCache
) : BaseCache() {

    init {
        cache.initialize()
    }

    private companion object {
        const val KEY = "radar"
        const val INDEX = 0
    }

    fun put(radar: Radar): Single<Either<Failure, Radar>> =
        Single.fromCallable {
            val editor = cache.edit(KEY)
            val sink = editor?.newSink(INDEX)
            val bufferedSink = sink?.buffer()
            val byteArray = serializerObject(radar)

            bufferedSink.use { _ ->
                bufferedSink?.write(byteArray)
            }
            editor?.commit()

            radar.asRight() as Either<Failure, Radar>
        }.doOnError {
            Timber.e("Disk cache insert failed: $it")
        }.onErrorReturn {
            Failure
                .DiskCacheLruWriteFailure
                .asLeft()
        }

    fun read(): Single<Either<Failure, Radar>> = Single.fromCallable {
        val snapshot: DiskLruCache.Snapshot = cache.get(KEY)
        val byteArray: ByteArray
        byteArray = snapshot.getSource(INDEX)
            .buffer()
            .readByteArray()

        deserializeBytes<Radar>(byteArray)
            .asRight() as Either<Failure, Radar>
    }.onErrorReturn {
        Failure
            .DiskCacheLruReadFailure
            .asLeft()
    }

    fun remove(): Single<Either<Failure, Boolean>> = Single.fromCallable {
        cache.remove(KEY).asRight() as Either<Failure, Boolean>
    }.onErrorReturn {
        Failure
            .DiskCacheEvictionFailure
            .asLeft()
    }
}