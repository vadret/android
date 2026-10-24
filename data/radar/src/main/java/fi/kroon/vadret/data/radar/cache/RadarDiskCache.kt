package fi.kroon.vadret.data.radar.cache

import fi.kroon.vadret.data.common.BaseCache
import fi.kroon.vadret.data.common.DiskCache
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class RadarDiskCache(
    private val cache: DiskCache
) : BaseCache() {

    private companion object {
        const val KEY = "radar"
    }

    suspend fun put(radar: Radar): Either<Failure, Radar> =
        withContext(Dispatchers.IO) {
            try {
                cache.write(KEY, serializerObject(radar))
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
                deserializeBytes<Radar>(cache.read(KEY)!!)
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