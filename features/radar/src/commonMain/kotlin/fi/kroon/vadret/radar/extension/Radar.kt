package fi.kroon.vadret.radar.extension

import fi.kroon.vadret.radar.model.Radar
import fi.kroon.vadret.radar.model.RadarFrame
import fi.kroon.vadret.radar.model.RadarImage
import fi.kroon.vadret.ui.extension.toElapsedTime
import fi.kroon.vadret.ui.format.DateTextFormatter
import kotlinx.datetime.TimeZone
import kotlin.time.Instant

internal fun Radar.toRadarFrames(
    now: Instant,
    timeZone: TimeZone,
    languageTag: String
): List<RadarFrame> {
    val weekdayFormatter: DateTextFormatter = weekdayFormatter(
        timeZone = timeZone,
        languageTag = languageTag
    )
    return images.map { radarImage: RadarImage ->
        RadarFrame(
            imageUrl = radarImage.imageUrl,
            timeText = radarImage.validAt.toTimeText(timeZone = timeZone),
            weekday = weekdayFormatter.format(instant = radarImage.validAt),
            elapsedTime = radarImage.validAt.toElapsedTime(now = now)
        )
    }
}