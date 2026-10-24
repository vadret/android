package fi.kroon.vadret.forecastwarning.extension

import fi.kroon.vadret.aggregatedfeed.model.FeedSource

private const val KRISINFORMATION_NAME = "Krisinformation"
private const val SMHI_NAME = "SMHI"

internal fun FeedSource.displayName(): String =
    when (this) {
        FeedSource.KRISINFORMATION -> KRISINFORMATION_NAME
        FeedSource.SMHI -> SMHI_NAME
    }