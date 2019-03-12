package fi.kroon.vadret.utils.common

import fi.kroon.vadret.R
import fi.kroon.vadret.presentation.alert.model.TimeStampStringResourceAndDiff
import fi.kroon.vadret.utils.HOUR_IN_MILLIS
import fi.kroon.vadret.utils.MINUTE_IN_MILLIS
import fi.kroon.vadret.utils.A_SECOND_IN_MILLIS
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

    fun toMomentsAgo(dateString: String): TimeStampStringResourceAndDiff {

        val date: Long = OffsetDateTime.parse(dateString).toEpochSecond()

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

        return TimeStampStringResourceAndDiff(
            diffInMillis = diff,
            momentResourceId = moment
        )
    }
}