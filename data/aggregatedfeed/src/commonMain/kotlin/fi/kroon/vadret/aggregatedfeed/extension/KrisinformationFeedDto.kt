package fi.kroon.vadret.aggregatedfeed.extension

import fi.kroon.vadret.aggregatedfeed.local.AggregatedFeedEntity
import fi.kroon.vadret.aggregatedfeed.local.FeedDetailEntity
import fi.kroon.vadret.aggregatedfeed.model.FeedDetailKind
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.aggregatedfeed.remote.KrisinformationFeedDto
import fi.kroon.vadret.logging.Logger
import fi.kroon.vadret.logging.LoggerFactory
import kotlin.time.Instant

private val logger: Logger = LoggerFactory.getLogger(tag = "fi.kroon.vadret.aggregatedfeed")

private const val UNTITLED = ""

internal fun KrisinformationFeedDto.toAggregatedFeedEntity(): AggregatedFeedEntity? {
    val publishedAt: Instant? = Instant.parseOrNull(input = published)
    if (publishedAt == null) {
        logger.warn { "Skipping Krisinformation item $identifier, published $published" }
        return null
    }
    return AggregatedFeedEntity(
        identifier = "krisinformation-$identifier",
        headline = headline,
        details = listOfNotNull(
            preamble.toFeedDetailEntity(feedDetailKind = FeedDetailKind.SUMMARY),
            bodyText.toFeedDetailEntity(feedDetailKind = FeedDetailKind.BODY)
        ),
        source = FeedSource.KRISINFORMATION.name,
        publishedAtEpochMillis = publishedAt.toEpochMilliseconds()
    )
}

private fun String?.toFeedDetailEntity(feedDetailKind: FeedDetailKind): FeedDetailEntity? =
    if (isNullOrBlank()) {
        null
    } else {
        FeedDetailEntity(
            kind = feedDetailKind.name,
            title = UNTITLED,
            text = trim()
        )
    }