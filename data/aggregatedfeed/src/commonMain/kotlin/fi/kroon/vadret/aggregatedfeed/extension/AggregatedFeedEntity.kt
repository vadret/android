package fi.kroon.vadret.aggregatedfeed.extension

import fi.kroon.vadret.aggregatedfeed.local.AggregatedFeedEntity
import fi.kroon.vadret.aggregatedfeed.local.FeedDetailEntity
import fi.kroon.vadret.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.aggregatedfeed.model.FeedDetail
import fi.kroon.vadret.aggregatedfeed.model.FeedDetailKind
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.aggregatedfeed.model.WarningLevel
import fi.kroon.vadret.logging.Logger
import fi.kroon.vadret.logging.LoggerFactory
import kotlin.time.Instant

private val logger: Logger = LoggerFactory.getLogger(tag = "fi.kroon.vadret.aggregatedfeed")

internal fun AggregatedFeedEntity.toAggregatedFeed(): AggregatedFeed? {
    val feedSource: FeedSource? = FeedSource.entries.firstOrNull { feedSource: FeedSource -> feedSource.name == source }
    if (feedSource == null) {
        logger.warn { "Skipping cached item $identifier from unknown source $source" }
        return null
    }
    return AggregatedFeed(
        identifier = identifier,
        headline = headline,
        areaName = areaName,
        warningLevel = WarningLevel.entries.firstOrNull { level: WarningLevel -> level.name == warningLevel },
        details = details.map { feedDetailEntity: FeedDetailEntity -> feedDetailEntity.toFeedDetail() },
        source = feedSource,
        publishedAt = Instant.fromEpochMilliseconds(epochMilliseconds = publishedAtEpochMillis)
    )
}

private fun FeedDetailEntity.toFeedDetail(): FeedDetail =
    FeedDetail(
        kind = FeedDetailKind.entries.firstOrNull { feedDetailKind: FeedDetailKind -> feedDetailKind.name == kind } ?: FeedDetailKind.OTHER,
        title = title,
        text = text
    )