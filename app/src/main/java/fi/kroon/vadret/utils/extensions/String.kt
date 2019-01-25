package fi.kroon.vadret.utils.extensions

import android.content.Context
import fi.kroon.vadret.R
import fi.kroon.vadret.utils.DAY_MILLIS
import fi.kroon.vadret.utils.HOUR_MILLIS
import fi.kroon.vadret.utils.MINUTE_MILLIS
import org.threeten.bp.OffsetDateTime
import timber.log.Timber

fun String.Companion.empty() = ""
fun String.splitByCommaTakeFirst(): String = this.split(",").get(0).trim()
fun String.splitBySpaceTakeFirst(): String = this.split(" ").get(0).trim()
fun String.splitToList(): List<String> = this.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toList()
fun String.parseToLocalDate() = OffsetDateTime.parse(this).toLocalDate()
fun String.toTimeAgo(context: Context): String {

    val date = OffsetDateTime.parse(this).toEpochSecond()
    Timber.d("Epoch is $date")

    var time = date
    if (time < 1000000000000L) {
        time *= 1000
    }

    val now = System.currentTimeMillis()
    if (time > now || time <= 0) {
        return context.getString(R.string.in_the_future)
    }

    val diff = now - time
    return when {
        diff < MINUTE_MILLIS -> context.getString(R.string.moments_ago)
        diff < 2 * MINUTE_MILLIS -> context.getString(R.string.a_minute_ago)
        diff < 60 * MINUTE_MILLIS -> "${diff / MINUTE_MILLIS} ${context.getString(R.string.minutes_ago)}"
        diff < 2 * HOUR_MILLIS -> context.getString(R.string.an_hour_ago)
        diff < 24 * HOUR_MILLIS -> "${diff / HOUR_MILLIS} ${context.getString(R.string.hours_ago)}"
        diff < 48 * HOUR_MILLIS -> context.getString(R.string.yesterday)
        else -> "${diff / DAY_MILLIS} ${context.getString(R.string.days_ago)}"
    }
}