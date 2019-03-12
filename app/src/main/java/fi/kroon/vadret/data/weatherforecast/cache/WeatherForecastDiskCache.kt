package fi.kroon.vadret.data.weatherforecast.cache

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.weatherforecast.model.Weather
import fi.kroon.vadret.presentation.weatherforecast.di.WeatherForecastScope
import fi.kroon.vadret.utils.extensions.asLeft
import fi.kroon.vadret.utils.extensions.asRight
import io.reactivex.Single
import okhttp3.internal.cache.DiskLruCache
import okio.buffer
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import javax.inject.Inject

@WeatherForecastScope
class WeatherForecastDiskCache @Inject constructor(
    private val cache: DiskLruCache
) {

    init {
        cache.initialize()
    }

    private companion object {

        const val KEY = "weatherforecast"
        const val INDEX = 0

        inline fun <reified T> deserializeBytes(bytes: ByteArray): T {
            val byteArrayInputStream = ByteArrayInputStream(bytes)

            val objectInputStream = ObjectInputStream(byteArrayInputStream)
            val objects: T = objectInputStream.readObject() as T

            objectInputStream.close()
            byteArrayInputStream.close()

            return objects
        }

        fun serializerObject(`object`: Any): ByteArray {
            val byteArrayOutputStream: ByteArrayOutputStream = ByteArrayOutputStream()

            val objectOutputStream: ObjectOutputStream = ObjectOutputStream(byteArrayOutputStream).apply {
                writeObject(`object`)
                flush()
            }

            val byteArray: ByteArray?
            byteArray = byteArrayOutputStream.toByteArray()
            byteArrayOutputStream.close()

            objectOutputStream.close()

            return byteArray
        }
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