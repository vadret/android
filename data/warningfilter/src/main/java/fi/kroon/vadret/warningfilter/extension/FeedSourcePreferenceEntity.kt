package fi.kroon.vadret.warningfilter.extension

import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.warningfilter.local.FeedSourcePreferenceEntity
import fi.kroon.vadret.warningfilter.model.FeedSourceOption

internal fun List<FeedSourcePreferenceEntity>.toFeedSourceOptions(defaultIsEnabled: Boolean): List<FeedSourceOption> =
    FeedSource.entries.map { feedSource: FeedSource ->
        FeedSourceOption(
            feedSource = feedSource,
            isEnabled = firstOrNull { feedSourcePreferenceEntity: FeedSourcePreferenceEntity ->
                feedSourcePreferenceEntity.feedSource == feedSource.name
            }?.isEnabled ?: defaultIsEnabled
        )
    }