package fi.kroon.vadret.weatherforecast.extension

import fi.kroon.vadret.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.ui.format.toDayTimeText
import fi.kroon.vadret.weatherforecast.model.LatestWarning
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.TimeZone
import kotlin.time.Instant

internal fun List<AggregatedFeed>.toLatestWarning(
    now: Instant,
    timeZone: TimeZone,
    languageTag: String
): LatestWarning? {
    val latest: AggregatedFeed = firstOrNull() ?: return null
    return LatestWarning(
        warningLevel = latest.warningLevel,
        source = latest.source,
        isPublicAlert = latest.isPublicAlert,
        headline = latest.headline,
        endsAtText = latest.endsAt?.toDayTimeText(
            now = now,
            timeZone = timeZone,
            languageTag = languageTag
        ),
        warningIdentifiers = map { aggregatedFeed: AggregatedFeed -> aggregatedFeed.identifier }.toImmutableList()
    )
}