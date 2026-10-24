package fi.kroon.vadret.radar.extension

import fi.kroon.vadret.radar.model.Radar
import fi.kroon.vadret.radar.model.RadarFrame
import fi.kroon.vadret.radar.model.RadarImage
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class RadarTest {

    @Test
    fun `frames show the local time, past midnight too`() {
        val radar = Radar(
            images = listOf(
                RadarImage(imageUrl = "https://smhi.se/2350.png", validAt = Instant.parse(input = "2026-10-24T21:50:00Z")),
                RadarImage(imageUrl = "https://smhi.se/0055.png", validAt = Instant.parse(input = "2026-10-24T22:55:00Z"))
            )
        )

        assertEquals(
            expected = listOf(
                RadarFrame(imageUrl = "https://smhi.se/2350.png", timeText = "23:50"),
                RadarFrame(imageUrl = "https://smhi.se/0055.png", timeText = "00:55")
            ),
            actual = radar.toRadarFrames(timeZone = TimeZone.of(zoneId = "Europe/Stockholm"))
        )
    }
}