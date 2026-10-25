package fi.kroon.vadret.radar.extension

import fi.kroon.vadret.radar.model.Radar
import fi.kroon.vadret.radar.model.RadarFrame
import fi.kroon.vadret.radar.model.RadarImage
import fi.kroon.vadret.ui.extension.toElapsedTime
import kotlinx.datetime.TimeZone
import java.text.DateFormat
import java.util.Locale
import kotlin.time.Instant

internal fun Radar.toRadarFrames(
    now: Instant,
    timeZone: TimeZone,
    locale: Locale
): List<RadarFrame> {
    val weekdayFormat: DateFormat = weekdayFormat(
        timeZone = timeZone,
        locale = locale
    )
    return images.map { radarImage: RadarImage ->
        RadarFrame(
            imageUrl = radarImage.imageUrl,
            timeText = radarImage.validAt.toTimeText(timeZone = timeZone),
            weekday = radarImage.validAt.toWeekday(weekdayFormat = weekdayFormat),
            elapsedTime = radarImage.validAt.toElapsedTime(now = now)
        )
    }
}