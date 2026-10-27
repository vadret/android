package fi.kroon.vadret.radar.extension

import fi.kroon.vadret.radar.model.WindTimeFrame
import fi.kroon.vadret.ui.format.DateTextFormatter
import fi.kroon.vadret.wind.model.WindForecast
import fi.kroon.vadret.wind.model.WindFrame
import kotlinx.datetime.TimeZone

internal fun WindForecast.toWindTimeFrames(
    timeZone: TimeZone,
    languageTag: String
): List<WindTimeFrame> {
    val weekdayFormatter: DateTextFormatter = weekdayFormatter(
        timeZone = timeZone,
        languageTag = languageTag
    )
    return frames.map { windFrame: WindFrame ->
        WindTimeFrame(
            timeText = windFrame.validAt.toTimeText(timeZone = timeZone),
            weekday = weekdayFormatter.format(instant = windFrame.validAt)
        )
    }
}