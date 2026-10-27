package fi.kroon.vadret.warningfilter.extension

import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.warningfilter.local.FeedSourcePreferenceEntity

internal fun FeedSource.toFeedSourcePreferenceEntity(isEnabled: Boolean): FeedSourcePreferenceEntity =
    FeedSourcePreferenceEntity(
        feedSource = name,
        isEnabled = isEnabled
    )