package fi.kroon.vadret.common

import fi.kroon.vadret.utils.extensions.toWindChill
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class WindChillTest {

    @Test
    fun `assert toWindChill produces expected results`() {
        val temperature = -20.0
        val windSpeed = 1.388889 // 5km/h in m/s

        val windChill = temperature.toWindChill(windSpeed).toDouble()
        assertThat(windChill).isEqualTo(-24.3)
    }

    @Test
    fun ` toWindChill produces expected -33 degree result`() {
        val temperature = -20.0
        val windSpeed = 8.333333 // 30km/h in m/s

        val windChill = temperature.toWindChill(windSpeed).toDouble()
        assertThat(windChill).isEqualTo(-32.6)
    }
}