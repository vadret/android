package fi.kroon.vadret.data.weatherforecast.cache

import fi.kroon.vadret.data.common.BaseCacheImpl
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.weatherforecast.model.Weather
import fi.kroon.vadret.presentation.weatherforecast.di.WeatherForecastFeatureScope
import fi.kroon.vadret.utils.extensions.asLeft
import fi.kroon.vadret.utils.extensions.asRight
import io.reactivex.Single
import okhttp3.internal.cache.DiskLruCache
import okio.buffer
import timber.log.Timber
import javax.inject.Inject

@WeatherForecastFeatureScope
class WeatherForecastDiskCacheImpl @Inject constructor(
    private val cache: DiskLruCache
) : BaseCacheImpl() {

    init {
        cache.initialize()
    }

    private companion object {
        const val KEY = "weatherforecast"
        const val INDEX = 0
    }

    fun put(weather: Weather): Single<Either<Failure, Weather>> =
        Single.fromCallable {
            val editor = cache.edit(KEY)
            val sink = editor?.newSink(INDEX)
            val bufferedSink = sink?.buffer()
            val byteArray = serializerObject(weather)

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

    fun read(): Single<Either<Failure, Weather>> = Single.fromCallable {
        val snapshot: DiskLruCache.Snapshot = cache.get(KEY)
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

    fun remove(): Single<Either<Failure, Boolean>> = Single.fromCallable {
        cache.remove(KEY).asRight() as Either<Failure, Boolean>
    }.onErrorReturn {
        Failure
            .DiskCacheEvictionFailure
            .asLeft()
    }
}