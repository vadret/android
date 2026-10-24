package fi.kroon.vadret.forecastwarning.extension

import fi.kroon.vadret.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.core.R
import fi.kroon.vadret.forecastwarning.model.ElapsedTime
import fi.kroon.vadret.forecastwarning.model.WarningItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class AggregatedFeedTest {

    private val now: Instant = Instant.parse(input = "2026-10-24T08:30:00Z")

    @Test
    fun `a feed item becomes a warning with spaced areas and its age`() {
        val aggregatedFeed = AggregatedFeed(
            identifier = "smhi-1-1",
            headline = "Gul varning: Vind",
            preamble = "Gotland,Kalmar, Öland",
            bodyText = "Hårda vindbyar",
            source = FeedSource.SMHI,
            publishedAt = Instant.parse(input = "2026-10-24T10:00:00+02:00")
        )

        assertEquals(
            expected = WarningItem(
                identifier = "smhi-1-1",
                headline = "Gul varning: Vind",
                bodyText = "Hårda vindbyar",
                preamble = "Gotland, Kalmar, Öland",
                source = FeedSource.SMHI,
                elapsedTime = ElapsedTime(
                    resourceId = R.string.minutes_ago,
                    count = 30
                )
            ),
            actual = aggregatedFeed.toWarningItem(now = now)
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