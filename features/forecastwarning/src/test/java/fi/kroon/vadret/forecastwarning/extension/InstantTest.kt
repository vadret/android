package fi.kroon.vadret.forecastwarning.extension

import fi.kroon.vadret.core.R
import fi.kroon.vadret.forecastwarning.model.ElapsedTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class InstantTest {

    private val now: Instant = Instant.parse(input = "2026-10-24T08:30:00Z")

    @Test
    fun `elapsed time picks the closest unit, edges included`() {
        val cases: Map<Duration, ElapsedTime> = mapOf(
            (-1).seconds to ElapsedTime(resourceId = R.string.in_the_future),
            0.seconds to ElapsedTime(resourceId = R.string.moments_ago),
            59.seconds to ElapsedTime(resourceId = R.string.moments_ago),
            1.minutes to ElapsedTime(resourceId = R.string.a_minute_ago),
            119.seconds to ElapsedTime(resourceId = R.string.a_minute_ago),
            2.minutes to ElapsedTime(resourceId = R.string.minutes_ago, count = 2),
            59.minutes to ElapsedTime(resourceId = R.string.minutes_ago, count = 59),
            1.hours to ElapsedTime(resourceId = R.string.an_hour_ago),
            119.minutes to ElapsedTime(resourceId = R.string.an_hour_ago),
            2.hours to ElapsedTime(resourceId = R.string.hours_ago, count = 2),
            23.hours to ElapsedTime(resourceId = R.string.hours_ago, count = 23),
            1.days to ElapsedTime(resourceId = R.string.yesterday),
            47.hours to ElapsedTime(resourceId = R.string.yesterday),
            2.days to ElapsedTime(resourceId = R.string.days_ago, count = 2),
            30.days to ElapsedTime(resourceId = R.string.days_ago, count = 30)
        )

        assertEquals(
            expected = cases,
            actual = cases.keys.associateWith { elapsed: Duration -> (now - elapsed).toElapsedTime(now = now) }
        )
    }
}