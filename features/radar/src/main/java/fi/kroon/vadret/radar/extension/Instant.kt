package fi.kroon.vadret.radar.extension

import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Instant
import java.util.TimeZone as JavaTimeZone

private const val WEEKDAY_PATTERN = "EEEE"

private val timeFormat = LocalTime.Format {
    hour()
    char(value = ':')
    minute()
}

internal fun Instant.toTimeText(timeZone: TimeZone): String =
    toLocalDateTime(timeZone = timeZone)
        .time
        .format(format = timeFormat)

internal fun weekdayFormat(
    timeZone: TimeZone,
    locale: Locale
): DateFormat {
    val weekdayFormat = SimpleDateFormat(WEEKDAY_PATTERN, locale)
    weekdayFormat.timeZone = JavaTimeZone.getTimeZone(timeZone.id)
    return weekdayFormat
}

internal fun Instant.toWeekday(weekdayFormat: DateFormat): String = weekdayFormat.format(Date(toEpochMilliseconds()))