package fi.kroon.vadret.radar.extension

import fi.kroon.vadret.core.R
import fi.kroon.vadret.radar.model.Radar
import fi.kroon.vadret.radar.model.RadarFrame
import fi.kroon.vadret.radar.model.RadarImage
import fi.kroon.vadret.radar.model.WindTimeFrame
import fi.kroon.vadret.ui.model.ElapsedTime
import fi.kroon.vadret.wind.model.WindForecast
import fi.kroon.vadret.wind.model.WindFrame
import fi.kroon.vadret.wind.model.WindGrid
import kotlinx.datetime.TimeZone
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

private val stockholm: TimeZone = TimeZone.of(zoneId = "Europe/Stockholm")

class RadarTest {

    @Test
    fun `frames show the local time, weekday and age, past midnight too`() {
        val radar = Radar(
            images = listOf(
                RadarImage(imageUrl = "https://smhi.se/2350.png", validAt = Instant.parse(input = "2026-10-24T21:50:00Z")),
                RadarImage(imageUrl = "https://smhi.se/0055.png", validAt = Instant.parse(input = "2026-10-24T22:55:00Z"))
            )
        )

        assertEquals(
            expected = listOf(
                RadarFrame(
                    imageUrl = "https://smhi.se/2350.png",
                    timeText = "23:50",
                    weekday = "Saturday",
                    elapsedTime = ElapsedTime(resourceId = R.string.an_hour_ago)
                ),
                RadarFrame(
                    imageUrl = "https://smhi.se/0055.png",
                    timeText = "00:55",
                    weekday = "Sunday",
                    elapsedTime = ElapsedTime(resourceId = R.string.minutes_ago, count = 15)
                )
            ),
            actual = radar.toRadarFrames(
                now = Instant.parse(input = "2026-10-24T23:10:00Z"),
                timeZone = stockholm,
                locale = Locale.ENGLISH
            )
        )
    }

    @Test
    fun `wind hours show the local time and weekday`() {
        val windForecast = WindForecast(
            referenceTime = Instant.parse(input = "2026-10-24T21:30:00Z"),
            grid = WindGrid(
                columnCount = 1,
                rowCount = 1,
                longitudes = listOf(18.0),
                latitudes = listOf(59.3)
            ),
            frames = listOf("2026-10-24T21:00:00Z", "2026-10-24T22:00:00Z").map { validAt: String ->
                WindFrame(
                    validAt = Instant.parse(input = validAt),
                    windSpeeds = listOf(1f),
                    windFromDirections = listOf(0f)
                )
            }
        )

        assertEquals(
            expected = listOf(
                WindTimeFrame(timeText = "23:00", weekday = "lördag"),
                WindTimeFrame(timeText = "00:00", weekday = "söndag")
            ),
            actual = windForecast.toWindTimeFrames(
                timeZone = stockholm,
                locale = Locale.forLanguageTag("sv-SE")
            )
        )
    }
}