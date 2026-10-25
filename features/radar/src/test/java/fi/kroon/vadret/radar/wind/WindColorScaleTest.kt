package fi.kroon.vadret.radar.wind

import kotlin.test.Test
import kotlin.test.assertEquals

class WindColorScaleTest {

    @Test
    fun `stops keep their exact colors`() {
        assertEquals(expected = 0xFF3B46A0.toInt(), actual = WindColorScale.argb(windSpeed = 0f))
        assertEquals(expected = 0xFF50B446.toInt(), actual = WindColorScale.argb(windSpeed = 8f))
        assertEquals(expected = 0xFF963CAA.toInt(), actual = WindColorScale.argb(windSpeed = 20f))
    }

    @Test
    fun `speeds between stops blend them`() {
        assertEquals(expected = 0xFF3D5AAF.toInt(), actual = WindColorScale.argb(windSpeed = 1f))
    }

    @Test
    fun `speeds outside the scale use its ends`() {
        assertEquals(expected = 0xFF3B46A0.toInt(), actual = WindColorScale.argb(windSpeed = -1f))
        assertEquals(expected = 0xFF6E37A0.toInt(), actual = WindColorScale.argb(windSpeed = 40f))
    }
}