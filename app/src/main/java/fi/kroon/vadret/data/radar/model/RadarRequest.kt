package fi.kroon.vadret.data.radar.model

import fi.kroon.vadret.utils.DEFAULT_TIME_ZONE
import fi.kroon.vadret.utils.EXPERIMENTAL_FILE_FORMAT
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset

data class RadarRequest(
    val year: String = LocalDate.now(ZoneOffset.UTC).year.toString(),
    val month: String = LocalDate.now(ZoneOffset.UTC).monthValue.toString(),
    val date: String = LocalDate.now(ZoneOffset.UTC).dayOfMonth.toString(),
    val format: String = EXPERIMENTAL_FILE_FORMAT,
    val timeZone: String = DEFAULT_TIME_ZONE
)