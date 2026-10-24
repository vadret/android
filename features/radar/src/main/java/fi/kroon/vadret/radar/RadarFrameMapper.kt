package fi.kroon.vadret.radar

import fi.kroon.vadret.data.radar.model.File
import fi.kroon.vadret.data.radar.model.Format
import fi.kroon.vadret.radar.model.RadarFrame
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber

internal object RadarFrameMapper {

    /**
     *  SMHI writes the time an image is valid for as
     *  "2026-10-24 14:05", in UTC.
     */
    private val validFormat = LocalDateTime.Format {
        date(format = LocalDate.Formats.ISO)
        char(value = ' ')
        hour()
        char(value = ':')
        minute()
    }

    private val timeFormat = LocalTime.Format {
        hour()
        char(value = ':')
        minute()
    }

    /**
     *  Files without an image or a readable time are skipped.
     */
    operator fun invoke(files: List<File>, timeZone: TimeZone): List<RadarFrame> =
        files.mapNotNull { file: File ->
            val format: Format? = file.formats.firstOrNull()
            val valid: LocalDateTime? = validFormat.parseOrNull(input = file.valid)
            if (format != null && valid != null) {
                RadarFrame(
                    imageUrl = format.link,
                    timeText = valid
                        .toInstant(timeZone = TimeZone.UTC)
                        .toLocalDateTime(timeZone = timeZone)
                        .time
                        .format(format = timeFormat)
                )
            } else {
                Timber.w("Skipping radar file ${file.key}, valid ${file.valid}")
                null
            }
        }
}