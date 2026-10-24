package fi.kroon.vadret.forecastwarning

import fi.kroon.vadret.core.R
import fi.kroon.vadret.data.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.forecastwarning.model.ElapsedTime
import fi.kroon.vadret.forecastwarning.model.WarningItem
import fi.kroon.vadret.forecastwarning.model.WarningSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class WarningMapperTest {

    private val now: Instant = Instant.parse(input = "2026-10-24T08:30:00Z")

    @Test
    fun `published time with an offset is compared in utc`() {
        val warning: WarningItem = WarningMapper(
            entityList = listOf(
                aggregatedFeed(
                    published = "2026-10-24T10:00:00+02:00",
                    sourceId = 1,
                    preamble = "Gotland,Kalmar, Öland"
                )
            ),
            now = now
        ).single()

        assertEquals(
            expected = ElapsedTime(
                resourceId = R.string.minutes_ago,
                count = 30
            ),
            actual = warning.elapsedTime
        )
        assertEquals(
            expected = WarningSource.SMHI,
            actual = warning.source
        )
        assertEquals(
            expected = "Gotland, Kalmar, Öland",
            actual = warning.preamble
        )
    }

    @Test
    fun `elapsed time picks the closest unit`() {
        val cases: List<Pair<Duration, ElapsedTime>> = listOf(
            (-1).minutes to ElapsedTime(resourceId = R.string.in_the_future),
            30.seconds to ElapsedTime(resourceId = R.string.moments_ago),
            90.seconds to ElapsedTime(resourceId = R.string.a_minute_ago),
            30.minutes to ElapsedTime(
                resourceId = R.string.minutes_ago,
                count = 30
            ),
            90.minutes to ElapsedTime(resourceId = R.string.an_hour_ago),
            5.hours to ElapsedTime(
                resourceId = R.string.hours_ago,
                count = 5
            ),
            30.hours to ElapsedTime(resourceId = R.string.yesterday),
            3.days to ElapsedTime(
                resourceId = R.string.days_ago,
                count = 3
            )
        )
        cases.forEach { (elapsed: Duration, expected: ElapsedTime) ->
            assertEquals(
                expected = expected,
                actual = WarningMapper.toElapsedTime(
                    published = now - elapsed,
                    now = now
                ),
                message = "elapsed $elapsed"
            )
        }
    }

    @Test
    fun `feed sources map by id and by name`() {
        assertEquals(
            expected = WarningSource.KRISINFORMATION,
            actual = WarningMapper.toWarningSource(sourceId = 0)
        )
        assertEquals(
            expected = WarningSource.UNKNOWN,
            actual = WarningMapper.toWarningSource(sourceId = 2)
        )
        assertEquals(
            expected = WarningSource.SMHI,
            actual = WarningMapper.toWarningSource(name = "SMHI")
        )
        assertEquals(
            expected = WarningSource.UNKNOWN,
            actual = WarningMapper.toWarningSource(name = "Trafikverket")
        )
    }

    private fun aggregatedFeed(
        published: String,
        sourceId: Int,
        preamble: String
    ): AggregatedFeed =
        AggregatedFeed(
            identifier = "smhi-1-1",
            pushMessage = "",
            updated = published,
            published = published,
            headline = "Gul varning: Vind",
            preamble = preamble,
            bodyText = "Hårda vindbyar",
            areaList = emptyList(),
            language = "sv",
            event = "wind",
            push = false,
            sourceId = sourceId,
            isVma = false,
            isTestVma = false
        )
}