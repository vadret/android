package fi.kroon.vadret.radar

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.data.radar.model.RadarRequest
import fi.kroon.vadret.util.FIFTEEN_MINUTES_IN_MILLIS
import fi.kroon.vadret.util.common.IDateTimeUtil
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.onResult
import io.github.sphrak.either.flatMap
import io.github.sphrak.either.map
import timber.log.Timber
import javax.inject.Inject

class GetRadarImageUrlService @Inject constructor(
    private val getRadarImageUrlTask: GetRadarImageUrlTask,
    private val getRadarDiskCacheTask: GetRadarDiskCacheTask,
    private val getRadarMemoryCacheTask: GetRadarMemoryCacheTask,
    private val setRadarDiskCacheTask: SetRadarDiskCacheTask,
    private val setRadarMemoryCacheTask: SetRadarMemoryCacheTask,
    private val iDateTimeUtil: IDateTimeUtil
) : IDateTimeUtil by iDateTimeUtil {

    data class Data(
        val timeStamp: Long,
        val radar: Radar? = null,
        val radarRequest: RadarRequest = RadarRequest()
    )

    /**
     *  Today's radar images, from cache when [timeStamp]
     *  is less than fifteen minutes old.
     */
    suspend operator fun invoke(timeStamp: Long): Either<Failure, Data> =
        getRadar(Data(timeStamp = timeStamp))

    private suspend fun getRadar(data: Data): Either<Failure, Data> =
        with(data) {
            when {
                (currentTimeMillis() > (timeStamp + FIFTEEN_MINUTES_IN_MILLIS)) -> getRadarNet(data)
                else -> getRadarCache(data) ?: run {
                    Timber.d("Cache was empty, fetching radar from network.")
                    getRadarNet(data)
                }
            }
        }

    private suspend fun getRadarNet(data: Data): Either<Failure, Data> =
        getRadarImageUrlTask(data.radarRequest)
            .map { radar: Radar ->
                data.copy(radar = radar, timeStamp = currentTimeMillis())
            }.let { updateCache(it) }

    /**
     *  Memory cache first, then disk. Null when neither
     *  holds a radar with images.
     */
    private suspend fun getRadarCache(data: Data): Either<Failure, Data>? =
        (getRadarMemoryCacheTask().takeIf(::hasFiles) ?: getRadarDiskCacheTask().takeIf(::hasFiles))
            ?.map { radar: Radar ->
                Timber.d("Fetched Radar from cache: ${radar.files.size}")
                data.copy(radar = radar)
            }

    private fun hasFiles(result: Either<Failure, Radar>): Boolean =
        result.onResult(
            {
                false
            },
            { radar: Radar ->
                radar.files.isNotEmpty()
            }
        )

    private suspend fun updateCache(either: Either<Failure, Data>): Either<Failure, Data> =
        either.flatMap { data: Data ->
            Timber.i("Updating cache")
            val memoryEither: Either<Failure, Radar> = setRadarMemoryCacheTask(data.radar!!)
            val diskEither: Either<Failure, Radar> = setRadarDiskCacheTask(data.radar)
            memoryEither.flatMap {
                diskEither.map {
                    data
                }
            }
        }
}