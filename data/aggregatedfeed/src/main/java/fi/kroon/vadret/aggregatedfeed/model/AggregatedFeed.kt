package fi.kroon.vadret.aggregatedfeed.model

import kotlin.time.Instant

/**
 *  A warning from SMHI, or news or a VMA from Krisinformation.
 */
data class AggregatedFeed(
    val identifier: String,
    val headline: String,
    val preamble: String,
    val bodyText: String,
    val source: FeedSource,
    val publishedAt: Instant
)