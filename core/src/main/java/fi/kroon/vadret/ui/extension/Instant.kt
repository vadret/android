package fi.kroon.vadret.ui.extension

import fi.kroon.vadret.core.R
import fi.kroon.vadret.ui.model.ElapsedTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

private val momentsAgoLimit: Duration = 1.minutes
private val aMinuteAgoLimit: Duration = 2.minutes
private val minutesAgoLimit: Duration = 1.hours
private val anHourAgoLimit: Duration = 2.hours
private val hoursAgoLimit: Duration = 1.days
private val yesterdayLimit: Duration = 2.days

fun Instant.toElapsedTime(now: Instant): ElapsedTime {
    val elapsed: Duration = now - this
    return when {
        elapsed.isNegative() -> ElapsedTime(resourceId = R.string.in_the_future)

        elapsed < momentsAgoLimit -> ElapsedTime(resourceId = R.string.moments_ago)

        elapsed < aMinuteAgoLimit -> ElapsedTime(resourceId = R.string.a_minute_ago)

        elapsed < minutesAgoLimit -> ElapsedTime(
            resourceId = R.string.minutes_ago,
            count = elapsed.inWholeMinutes
        )

        elapsed < anHourAgoLimit -> ElapsedTime(resourceId = R.string.an_hour_ago)

        elapsed < hoursAgoLimit -> ElapsedTime(
            resourceId = R.string.hours_ago,
            count = elapsed.inWholeHours
        )

        elapsed < yesterdayLimit -> ElapsedTime(resourceId = R.string.yesterday)

        else -> ElapsedTime(
            resourceId = R.string.days_ago,
            count = elapsed.inWholeDays
        )
    }
}