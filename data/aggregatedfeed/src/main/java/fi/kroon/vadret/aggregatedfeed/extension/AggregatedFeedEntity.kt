package fi.kroon.vadret.aggregatedfeed.extension

import fi.kroon.vadret.aggregatedfeed.local.AggregatedFeedEntity
import fi.kroon.vadret.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.logging.Logger
import fi.kroon.vadret.logging.LoggerFactory
import kotlin.time.Instant

private val logger: Logger = LoggerFactory.getLogger(tag = "fi.kroon.vadret.aggregatedfeed")

/**
 *  Null for a source this version no longer knows.
 */
internal fun AggregatedFeedEntity.toAggregatedFeed(): AggregatedFeed? {
    val feedSource: FeedSource? = FeedSource.entries.firstOrNull { feedSource: FeedSource -> feedSource.name == source }
    if (feedSource == null) {
        logger.warn { "Skipping cached item $identifier from unknown source $source" }
        return null
    }
    return AggregatedFeed(
        identifier = identifier,
        headline = headline,
        preamble = preamble,
        bodyText = bodyText,
        source = feedSource,
        publishedAt = Instant.fromEpochMilliseconds(epochMilliseconds = publishedAtEpochMillis)
    )
}