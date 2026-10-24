package fi.kroon.vadret.warningfilter.model

import fi.kroon.vadret.aggregatedfeed.model.FeedSource

data class FeedSourceOption(
    val feedSource: FeedSource,
    val isEnabled: Boolean
)