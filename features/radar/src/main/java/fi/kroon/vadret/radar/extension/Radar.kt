package fi.kroon.vadret.radar.extension

import fi.kroon.vadret.radar.model.Radar
import fi.kroon.vadret.radar.model.RadarFrame
import fi.kroon.vadret.radar.model.RadarImage
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

private val timeFormat = LocalTime.Format {
    hour()
    char(value = ':')
    minute()
}

internal fun Radar.toRadarFrames(timeZone: TimeZone): List<RadarFrame> =
    images.map { radarImage: RadarImage ->
        RadarFrame(
            imageUrl = radarImage.imageUrl,
            timeText = radarImage.validAt
                .toLocalDateTime(timeZone = timeZone)
                .time
                .format(format = timeFormat)
        )
    }