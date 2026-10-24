package fi.kroon.vadret.radar

import fi.kroon.vadret.data.radar.model.File
import fi.kroon.vadret.data.radar.model.Format
import fi.kroon.vadret.radar.model.RadarFrame
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

class RadarFrameMapperTest {

    private val stockholm: TimeZone = TimeZone.of(zoneId = "Europe/Stockholm")

    @Test
    fun `smhi utc time is shown in local time`() {
        val frames: List<RadarFrame> = RadarFrameMapper(
            files = listOf(
                file(
                    valid = "2026-10-24 12:05",
                    links = listOf("https://example.com/1205.png")
                ),
                file(
                    valid = "2026-10-25 23:55",
                    links = listOf("https://example.com/2355.png")
                )
            ),
            timeZone = stockholm
        )

        assertEquals(
            expected = listOf(
                RadarFrame(
                    imageUrl = "https://example.com/1205.png",
                    timeText = "14:05"
                ),
                RadarFrame(
                    imageUrl = "https://example.com/2355.png",
                    timeText = "00:55"
                )
            ),
            actual = frames
        )
    }

    @Test
    fun `files without an image or a readable time are skipped`() {
        val frames: List<RadarFrame> = RadarFrameMapper(
            files = listOf(
                file(
                    valid = "2026-10-24 12:10",
                    links = emptyList()
                ),
                file(
                    valid = "2026-10-24T12:15:00Z",
                    links = listOf("https://example.com/1215.png")
                )
            ),
            timeZone = stockholm
        )

        assertEquals(
            expected = emptyList(),
            actual = frames
        )
    }

    private fun file(valid: String, links: List<String>): File =
        File(
            key = valid,
            valid = valid,
            formats = links.map { link: String ->
                Format(
                    key = "png",
                    updated = valid,
                    link = link
                )
            }
        )
}