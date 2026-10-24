package fi.kroon.vadret.common

import fi.kroon.vadret.util.common.WindChill
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class WindChillTest {

    @Test
    fun `real temperature -10c and windspeed 2 is -34c`() {
        val temperature = -10.0
        val windSpeed = 2.0

        val windChill = WindChill.calculate(temperature, windSpeed)
        assertThat(windChill).isEqualTo("-14.1")
    }

    @Test
    fun `real temperature -20c and windspeed is -34c`() {
        val temperature = -20.0
        val windSpeed = 10.0

        val windChill = WindChill.calculate(temperature, windSpeed)
        assertThat(windChill).isEqualTo("-33.6")
    }
}