package fi.kroon.vadret.data.radar.cache

import androidx.collection.LruCache
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.util.RADAR_CACHE_KEY
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import timber.log.Timber
import javax.inject.Inject

class RadarCacheDataSource @Inject constructor(
    private val diskCache: RadarDiskCache,
    private val memoryCache: LruCache<Long, Radar>
) {

    fun getMemoryCache(): Either<Failure, Radar> =
        memoryCache
            .get(RADAR_CACHE_KEY)
            ?.asRight()
            ?: Failure
                .MemoryCacheLruReadFailure
                .asLeft()

    suspend fun getDiskCache(): Either<Failure, Radar> = diskCache
        .read()

    fun updateMemoryCache(radar: Radar): Either<Failure, Radar> {
        memoryCache.put(RADAR_CACHE_KEY, radar)
        return radar.asRight()
    }

    suspend fun updateDiskCache(radar: Radar): Either<Failure, Radar> =
        diskCache.put(radar)

    fun clearMemoryCache(): Either<Failure, Unit> {
        memoryCache
            .evictAll()
        return Unit.asRight()
    }

    suspend fun clearDiskCache(): Either<Failure, Unit> =
        when (diskCache.remove()) {
            is Either.Right -> Unit.asRight()
            is Either.Left -> {
                Timber.e("Disk cache eviction failed")
                Failure
                    .DiskCacheEvictionFailure
                    .asLeft()
            }
        }
}