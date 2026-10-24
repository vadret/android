package fi.kroon.vadret.data.radar.model

import fi.kroon.vadret.util.DEFAULT_RADAR_FILE_FORMAT
import fi.kroon.vadret.util.DEFAULT_TIME_ZONE
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/**
 *  SMHI publishes the radar per UTC day.
 */
data class RadarRequest(
    private val today: LocalDate = Clock.System.todayIn(TimeZone.UTC),
    val year: String = today.year.toString(),
    val month: String = today.month.number.toString(),
    val date: String = today.day.toString(),
    val format: String = DEFAULT_RADAR_FILE_FORMAT,
    val timeZone: String = DEFAULT_TIME_ZONE
)