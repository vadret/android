package fi.kroon.vadret.util.common

import fi.kroon.vadret.R
import fi.kroon.vadret.presentation.warning.display.model.ElapsedTime
import fi.kroon.vadret.util.HOUR_IN_MILLIS
import fi.kroon.vadret.util.MINUTE_IN_MILLIS
import fi.kroon.vadret.util.A_SECOND_IN_MILLIS
import org.threeten.bp.OffsetDateTime

object DateTimeStringUtil {

    /**
     *  Translates a timestamp into a human
     *  readable 'moment' style string resource.
     *
     *  Example:
     *  > minute ago
     *  > moments ago
     */

    const val EPOCH_START_IN_MILLIS_LONG = 1_000_000_000_000L

    fun toElapsedTime(published: String): ElapsedTime {

        val date: Long = OffsetDateTime.parse(published).toEpochSecond()

        var time: Long = date

        if (time < EPOCH_START_IN_MILLIS_LONG) {
            time *= A_SECOND_IN_MILLIS
        }

        val now: Long = System.currentTimeMillis()
        val diff: Long = now - time

        val moment: Int = when {
            (time > now || 0 >= time) -> R.string.in_the_future
            (diff < MINUTE_IN_MILLIS) -> R.string.moments_ago
            (diff < 2 * MINUTE_IN_MILLIS) -> R.string.a_minute_ago
            (diff < 60 * MINUTE_IN_MILLIS) -> R.string.minutes_ago
            (diff < 2 * HOUR_IN_MILLIS) -> R.string.an_hour_ago
            (diff < 24 * HOUR_IN_MILLIS) -> R.string.hours_ago
            (diff < 48 * HOUR_IN_MILLIS) -> R.string.yesterday
            else -> R.string.days_ago
        }

        return ElapsedTime(
            diffInMillis = diff,
            resId = moment
        )
    }
}