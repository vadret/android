package fi.kroon.vadret.forecastwarning.model

import fi.kroon.vadret.aggregatedfeed.model.FeedSource

data class WarningItem(
    val identifier: String,
    val headline: String,
    val bodyText: String,
    val preamble: String,
    val source: FeedSource,
    val elapsedTime: ElapsedTime
)