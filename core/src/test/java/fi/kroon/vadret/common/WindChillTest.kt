package fi.kroon.vadret.common

import fi.kroon.vadret.util.common.WindChill
import kotlin.test.Test
import kotlin.test.assertEquals

class WindChillTest {

    @Test
    fun `real temperature -10c and windspeed 2 is -34c`() {
        val temperature = -10.0
        val windSpeed = 2.0

        val windChill = WindChill.calculate(temperature, windSpeed)
        assertEquals(expected = "-14.1", actual = windChill)
    }

    @Test
    fun `real temperature -20c and windspeed is -34c`() {
        val temperature = -20.0
        val windSpeed = 10.0

        val windChill = WindChill.calculate(temperature, windSpeed)
        assertEquals(expected = "-33.6", actual = windChill)
    }
}