package fi.kroon.vadret.aggregatedfeed.local

import fi.kroon.vadret.common.DiskCache
import fi.kroon.vadret.common.defaultJson
import fi.kroon.vadret.logging.Logger
import fi.kroon.vadret.logging.LoggerFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException

private const val CACHE_KEY = "aggregatedfeed.json"

private val logger: Logger = LoggerFactory.getLogger(tag = "fi.kroon.vadret.aggregatedfeed")

internal class DefaultAggregatedFeedLocalDataSource(
    private val diskCache: DiskCache,
    private val coroutineDispatcher: CoroutineDispatcher
) : AggregatedFeedLocalDataSource {

    override suspend fun getAggregatedFeed(): AggregatedFeedCacheEntity? =
        withContext(context = coroutineDispatcher) {
            val text: String? = diskCache.read(key = CACHE_KEY)
            if (text != null) {
                decode(text = text)
            } else {
                null
            }
        }

    override suspend fun setAggregatedFeed(aggregatedFeedCacheEntity: AggregatedFeedCacheEntity) {
        withContext(context = coroutineDispatcher) {
            diskCache.write(
                key = CACHE_KEY,
                value = defaultJson.encodeToString(value = aggregatedFeedCacheEntity)
            )
        }
    }

    private fun decode(text: String): AggregatedFeedCacheEntity? =
        try {
            defaultJson.decodeFromString<AggregatedFeedCacheEntity>(string = text)
        } catch (serializationException: SerializationException) {
            logger.warn { "Dropping unreadable feed cache: ${serializationException.message}" }
            diskCache.remove(key = CACHE_KEY)
            null
        }
}