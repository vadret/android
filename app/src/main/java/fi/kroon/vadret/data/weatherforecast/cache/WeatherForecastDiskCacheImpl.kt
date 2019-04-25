package fi.kroon.vadret.data.weatherforecast.cache

import fi.kroon.vadret.data.common.BaseCacheImpl
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.weatherforecast.model.Weather
import fi.kroon.vadret.utils.extensions.asLeft
import fi.kroon.vadret.utils.extensions.asRight
import io.reactivex.Single
import okhttp3.internal.cache.DiskLruCache
import okio.BufferedSink
import okio.Sink
import okio.buffer
import timber.log.Timber
import javax.inject.Inject

class WeatherForecastDiskCacheImpl @Inject constructor(
    private val cache: DiskLruCache
) : BaseCacheImpl() {

    init {
        cache.initialize()
    }

    private companion object {
        const val INDEX = 0
    }

    fun put(cacheKey: String, weather: Weather): Single<Either<Failure, Weather>> =
        Single.fromCallable {
            val editor: DiskLruCache.Editor? = cache.edit(cacheKey)
            val sink: Sink? = editor?.newSink(INDEX)
            val bufferedSink: BufferedSink? = sink?.buffer()
            val byteArray: ByteArray = serializerObject(weather)

            bufferedSink.use { _ ->
                bufferedSink?.write(byteArray)
            }
            editor?.commit()

            weather.asRight() as Either<Failure, Weather>
        }.doOnError {
            Timber.e("Disk cache put failed: $it")
        }.onErrorReturn {
            Failure
                .DiskCacheLruWriteFailure
                .asLeft()
        }

    fun read(cacheKey: String): Single<Either<Failure, Weather>> = Single.fromCallable {
        val snapshot: DiskLruCache.Snapshot = cache.get(cacheKey)
        val byteArray: ByteArray
        byteArray = snapshot.getSource(INDEX)
            .buffer()
            .readByteArray()

        deserializeBytes<Weather>(byteArray)
            .asRight() as Either<Failure, Weather>
    }.onErrorReturn {
        Failure
            .DiskCacheLruReadFailure
            .asLeft()
    }

    fun remove(cacheKey: String): Single<Either<Failure, Boolean>> = Single.fromCallable {
        cache
            .remove(cacheKey)
            .asRight() as Either<Failure, Boolean>
    }.onErrorReturn {
        Failure
            .DiskCacheEvictionFailure
            .asLeft()
    }
}