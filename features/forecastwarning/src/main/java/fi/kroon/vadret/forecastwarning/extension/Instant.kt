package fi.kroon.vadret.forecastwarning.extension

import fi.kroon.vadret.core.R
import fi.kroon.vadret.forecastwarning.model.ElapsedTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

private const val TWO = 2

internal fun Instant.toElapsedTime(now: Instant): ElapsedTime {
    val elapsed: Duration = now - this
    return when {
        elapsed.isNegative() -> ElapsedTime(resourceId = R.string.in_the_future)
        elapsed < 1.minutes -> ElapsedTime(resourceId = R.string.moments_ago)
        elapsed < TWO.minutes -> ElapsedTime(resourceId = R.string.a_minute_ago)
        elapsed < 1.hours -> ElapsedTime(
            resourceId = R.string.minutes_ago,
            count = elapsed.inWholeMinutes
        )
        elapsed < TWO.hours -> ElapsedTime(resourceId = R.string.an_hour_ago)
        elapsed < 1.days -> ElapsedTime(
            resourceId = R.string.hours_ago,
            count = elapsed.inWholeHours
        )
        elapsed < TWO.days -> ElapsedTime(resourceId = R.string.yesterday)
        else -> ElapsedTime(
            resourceId = R.string.days_ago,
            count = elapsed.inWholeDays
        )
    }
}