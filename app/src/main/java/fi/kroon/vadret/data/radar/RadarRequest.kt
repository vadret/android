package fi.kroon.vadret.data.radar

import fi.kroon.vadret.utils.DEFAULT_RADAR_FILE_FORMAT
import fi.kroon.vadret.utils.DEFAULT_TIME_ZONE
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset

data class RadarRequest(
    val year: String = LocalDate.now(ZoneOffset.UTC).year.toString(),
    val month: String = LocalDate.now(ZoneOffset.UTC).monthValue.toString(),
    val date: String = LocalDate.now(ZoneOffset.UTC).dayOfMonth.toString(),
    val format: String = DEFAULT_RADAR_FILE_FORMAT,
    val timeZone: String = DEFAULT_TIME_ZONE
)