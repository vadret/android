package fi.kroon.vadret.radar.local

import fi.kroon.vadret.common.DiskCache
import fi.kroon.vadret.common.defaultJson
import fi.kroon.vadret.logging.Logger
import fi.kroon.vadret.logging.LoggerFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException

private const val CACHE_KEY = "radar_geotiff.json"

private val logger: Logger = LoggerFactory.getLogger(tag = "fi.kroon.vadret.radar")

internal class DefaultRadarLocalDataSource(
    private val diskCache: DiskCache,
    private val coroutineDispatcher: CoroutineDispatcher
) : RadarLocalDataSource {

    override suspend fun getRadar(): RadarCacheEntity? =
        withContext(context = coroutineDispatcher) {
            val text: String? = diskCache.read(key = CACHE_KEY)
            if (text != null) {
                decode(text = text)
            } else {
                null
            }
        }

    override suspend fun setRadar(radarCacheEntity: RadarCacheEntity) {
        withContext(context = coroutineDispatcher) {
            diskCache.write(
                key = CACHE_KEY,
                value = defaultJson.encodeToString(value = radarCacheEntity)
            )
        }
    }

    private fun decode(text: String): RadarCacheEntity? =
        try {
            defaultJson.decodeFromString<RadarCacheEntity>(string = text)
        } catch (serializationException: SerializationException) {
            logger.warn { "Dropping unreadable radar cache: ${serializationException.message}" }
            diskCache.remove(key = CACHE_KEY)
            null
        }
}