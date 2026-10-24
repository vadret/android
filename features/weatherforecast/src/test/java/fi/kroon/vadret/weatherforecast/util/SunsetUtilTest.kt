package fi.kroon.vadret.weatherforecast.util

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

class SunsetUtilTest {

    private val stockholmLatitude = 59.33
    private val stockholmLongitude = 18.07
    private val kirunaLatitude = 67.85
    private val kirunaLongitude = 20.23
    private val tolerance: Duration = 2.minutes

    @Test
    fun `stockholm sunrise and sunset on 24 october 2026`() {
        val sunriseSunset: SunriseSunset? = SunsetUtil.getSunriseSunset(
            day = Instant.parse("2026-10-24T10:00:00Z"),
            latitude = stockholmLatitude,
            longitude = stockholmLongitude
        )

        assertNotNull(sunriseSunset)
        assertWithin(expected = Instant.parse("2026-10-24T05:47:00Z"), actual = sunriseSunset.sunrise)
        assertWithin(expected = Instant.parse("2026-10-24T15:18:00Z"), actual = sunriseSunset.sunset)
    }

    @Test
    fun `no sunrise during polar night in kiruna`() {
        val sunriseSunset: SunriseSunset? = SunsetUtil.getSunriseSunset(
            day = Instant.parse("2026-12-21T12:00:00Z"),
            latitude = kirunaLatitude,
            longitude = kirunaLongitude
        )

        assertNull(sunriseSunset)
    }

    @Test
    fun `no sunset during midnight sun in kiruna`() {
        val sunriseSunset: SunriseSunset? = SunsetUtil.getSunriseSunset(
            day = Instant.parse("2026-06-21T12:00:00Z"),
            latitude = kirunaLatitude,
            longitude = kirunaLongitude
        )

        assertNull(sunriseSunset)
    }

    private fun assertWithin(expected: Instant, actual: Instant) {
        val difference: Duration = (actual - expected).absoluteValue
        assertTrue(
            actual = difference <= tolerance,
            message = "expected $expected but was $actual"
        )
    }
}