package fi.kroon.vadret.radar.extension

import fi.kroon.vadret.radar.model.WindTimeFrame
import fi.kroon.vadret.wind.model.WindForecast
import fi.kroon.vadret.wind.model.WindFrame
import kotlinx.datetime.TimeZone
import java.text.DateFormat
import java.util.Locale

internal fun WindForecast.toWindTimeFrames(
    timeZone: TimeZone,
    locale: Locale
): List<WindTimeFrame> {
    val weekdayFormat: DateFormat = weekdayFormat(
        timeZone = timeZone,
        locale = locale
    )
    return frames.map { windFrame: WindFrame ->
        WindTimeFrame(
            timeText = windFrame.validAt.toTimeText(timeZone = timeZone),
            weekday = windFrame.validAt.toWeekday(weekdayFormat = weekdayFormat)
        )
    }
}