package fi.kroon.vadret.forecastwarning.ui

import fi.kroon.vadret.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.aggregatedfeed.model.FeedDetail
import fi.kroon.vadret.aggregatedfeed.model.FeedDetailKind
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.aggregatedfeed.model.WarningLevel
import fi.kroon.vadret.forecastwarning.WarningState
import fi.kroon.vadret.forecastwarning.extension.toWarningItem
import fi.kroon.vadret.forecastwarning.model.WarningItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.time.Instant

private val previewNow: Instant = Instant.parse(input = "2026-10-26T09:00:00Z")

private val previewFeeds: List<AggregatedFeed> = listOf(
    AggregatedFeed(
        identifier = "smhi-wind-gotland",
        headline = "Vind",
        areaName = "Gotlands län",
        warningLevel = WarningLevel.ORANGE,
        details = listOf(
            FeedDetail(
                kind = FeedDetailKind.WHAT_HAPPENS,
                title = "Vad händer",
                text = "Mycket hårda vindbyar, 30 m/s i byarna, under natten mot tisdag."
            ),
            FeedDetail(
                kind = FeedDetailKind.ADVICE,
                title = "Råd",
                text = "Förankra lösa föremål utomhus och undvik onödiga resor."
            )
        ),
        source = FeedSource.SMHI,
        publishedAt = Instant.parse(input = "2026-10-26T08:15:00Z")
    ),
    AggregatedFeed(
        identifier = "smhi-rain-vastra-gotaland",
        headline = "Regn",
        areaName = "Västra Götalands län",
        warningLevel = WarningLevel.YELLOW,
        details = listOf(
            FeedDetail(
                kind = FeedDetailKind.WHAT_HAPPENS,
                title = "Vad händer",
                text = "Skyfallsliknande regn, 40 mm på 3 timmar, under tisdagen."
            )
        ),
        source = FeedSource.SMHI,
        publishedAt = Instant.parse(input = "2026-10-26T06:00:00Z")
    ),
    AggregatedFeed(
        identifier = "krisinformation-storm",
        headline = "Varning för kraftiga vindar i södra och mellersta Sverige",
        areaName = null,
        warningLevel = null,
        details = listOf(
            FeedDetail(
                kind = FeedDetailKind.SUMMARY,
                title = "",
                text = "SMHI har utfärdat varningar för blåsigt väder med mycket hårda vindbyar i delar av landet."
            )
        ),
        source = FeedSource.KRISINFORMATION,
        publishedAt = Instant.parse(input = "2026-10-24T14:00:00Z")
    )
)

internal fun warningPreviewState(): WarningState {
    val warnings: ImmutableList<WarningItem> = previewFeeds
        .map { aggregatedFeed: AggregatedFeed -> aggregatedFeed.toWarningItem(now = previewNow) }
        .toImmutableList()
    return WarningState(
        warnings = warnings,
        isLoading = false
    )
}