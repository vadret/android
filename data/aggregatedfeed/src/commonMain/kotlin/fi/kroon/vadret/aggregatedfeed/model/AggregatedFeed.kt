package fi.kroon.vadret.aggregatedfeed.model

import kotlin.time.Instant

data class AggregatedFeed(
    val identifier: String,
    val headline: String,
    val areaName: String?,
    val warningLevel: WarningLevel?,
    val details: List<FeedDetail>,
    val source: FeedSource,
    val publishedAt: Instant
)