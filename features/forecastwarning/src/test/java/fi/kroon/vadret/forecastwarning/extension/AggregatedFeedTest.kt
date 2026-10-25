package fi.kroon.vadret.forecastwarning.extension

import fi.kroon.vadret.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.aggregatedfeed.model.FeedDetail
import fi.kroon.vadret.aggregatedfeed.model.FeedDetailKind
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.aggregatedfeed.model.WarningLevel
import fi.kroon.vadret.core.R
import fi.kroon.vadret.forecastwarning.model.WarningDetail
import fi.kroon.vadret.forecastwarning.model.WarningItem
import fi.kroon.vadret.ui.model.ElapsedTime
import kotlinx.collections.immutable.persistentListOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

private fun detail(
    kind: FeedDetailKind,
    text: String,
    title: String = "Rubrik"
): FeedDetail =
    FeedDetail(
        kind = kind,
        title = title,
        text = text
    )

class AggregatedFeedTest {

    private val now: Instant = Instant.parse(input = "2026-10-24T08:30:00Z")

    private fun aggregatedFeed(
        areaName: String? = "Gotland,Kalmar, Öland",
        details: List<FeedDetail> = emptyList()
    ): AggregatedFeed =
        AggregatedFeed(
            identifier = "smhi-1-1",
            headline = "Vind",
            areaName = areaName,
            warningLevel = WarningLevel.YELLOW,
            details = details,
            source = FeedSource.SMHI,
            publishedAt = Instant.parse(input = "2026-10-24T10:00:00+02:00")
        )

    @Test
    fun `a feed item becomes a warning with spaced areas and its age`() {
        assertEquals(
            expected = WarningItem(
                identifier = "smhi-1-1",
                headline = "Vind",
                areaName = "Gotland, Kalmar, Öland",
                warningLevel = WarningLevel.YELLOW,
                details = persistentListOf(),
                source = FeedSource.SMHI,
                elapsedTime = ElapsedTime(
                    resourceId = R.string.minutes_ago,
                    count = 30
                )
            ),
            actual = aggregatedFeed().toWarningItem(now = now)
        )
    }

    @Test
    fun `known details get a short label and unknown ones keep their title`() {
        val details: List<WarningDetail> = aggregatedFeed(
            details = listOf(
                detail(kind = FeedDetailKind.WHAT_HAPPENS, text = "Blåst"),
                detail(kind = FeedDetailKind.DESCRIPTION, text = "Byar"),
                detail(kind = FeedDetailKind.WHERE, text = "Kusten"),
                detail(kind = FeedDetailKind.ADVICE, text = "Stanna inne"),
                detail(kind = FeedDetailKind.COMMENT, text = "Osäkert"),
                detail(kind = FeedDetailKind.OTHER, text = "Text", title = "Ny rubrik")
            )
        ).toWarningItem(now = now).details

        assertEquals(
            expected = listOf(
                WarningDetail.Labelled(labelResourceId = R.string.warning_detail_what_happens, text = "Blåst"),
                WarningDetail.Labelled(labelResourceId = R.string.warning_detail_description, text = "Byar"),
                WarningDetail.Labelled(labelResourceId = R.string.warning_detail_where, text = "Kusten"),
                WarningDetail.Labelled(labelResourceId = R.string.warning_detail_advice, text = "Stanna inne"),
                WarningDetail.Labelled(labelResourceId = R.string.warning_detail_comment, text = "Osäkert"),
                WarningDetail.Titled(title = "Ny rubrik", text = "Text")
            ),
            actual = details
        )
    }

    @Test
    fun `untitled details become paragraphs and summaries get spaced commas`() {
        val details: List<WarningDetail> = aggregatedFeed(
            areaName = null,
            details = listOf(
                detail(kind = FeedDetailKind.SUMMARY, text = "Gotland,Kalmar"),
                detail(kind = FeedDetailKind.BODY, text = "1,5 meter")
            )
        ).toWarningItem(now = now).details

        assertEquals(
            expected = listOf(
                WarningDetail.Paragraph(text = "Gotland, Kalmar"),
                WarningDetail.Paragraph(text = "1,5 meter")
            ),
            actual = details
        )
    }

    @Test
    fun `a where detail that repeats the area is left out`() {
        val details: List<WarningDetail> = aggregatedFeed(
            areaName = "Sydvästra Östersjön",
            details = listOf(
                detail(kind = FeedDetailKind.WHERE, text = "Sydvästra Östersjön"),
                detail(kind = FeedDetailKind.DESCRIPTION, text = "Kuling")
            )
        ).toWarningItem(now = now).details

        assertEquals(
            expected = listOf(WarningDetail.Labelled(labelResourceId = R.string.warning_detail_description, text = "Kuling")),
            actual = details
        )
    }

    @Test
    fun `feed sources are named by their brand`() {
        assertEquals(
            expected = listOf("Krisinformation", "SMHI"),
            actual = FeedSource.entries.map { feedSource: FeedSource -> feedSource.displayName() }
        )
    }
}