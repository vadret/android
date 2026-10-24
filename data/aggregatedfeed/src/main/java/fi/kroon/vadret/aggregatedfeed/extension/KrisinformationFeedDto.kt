package fi.kroon.vadret.aggregatedfeed.extension

import fi.kroon.vadret.aggregatedfeed.local.AggregatedFeedEntity
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.aggregatedfeed.remote.KrisinformationFeedDto
import fi.kroon.vadret.logging.Logger
import fi.kroon.vadret.logging.LoggerFactory
import kotlin.time.Instant

private val logger: Logger = LoggerFactory.getLogger(tag = "fi.kroon.vadret.aggregatedfeed")

/**
 *  News and VMAs share identifiers, which keeps a VMA that is
 *  also news from showing twice. Null when the publish time is unreadable.
 */
internal fun KrisinformationFeedDto.toAggregatedFeedEntity(): AggregatedFeedEntity? {
    val publishedAt: Instant? = Instant.parseOrNull(input = published)
    if (publishedAt == null) {
        logger.warn { "Skipping Krisinformation item $identifier, published $published" }
        return null
    }
    return AggregatedFeedEntity(
        identifier = "krisinformation-$identifier",
        headline = headline,
        preamble = preamble.orEmpty(),
        bodyText = bodyText.orEmpty(),
        source = FeedSource.KRISINFORMATION.name,
        publishedAtEpochMillis = publishedAt.toEpochMilliseconds()
    )
}