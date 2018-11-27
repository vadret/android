package fi.kroon.vadret.common

import fi.kroon.vadret.BaseUnitTest
import fi.kroon.vadret.utils.extensions.toCoordinate
import org.assertj.core.api.Assertions
import org.junit.Test

class DoubleTest : BaseUnitTest() {

    // / DOUBLE => DOUBLE

    @Test
    fun `0,0 Double coordinate is truncated and reduced to six decimal places`() {
        val doubleCoordinate = 0.00000000000000000000000
        Assertions.assertThat(doubleCoordinate.toCoordinate()).isEqualTo(0.000000)
    }

    @Test
    fun `-0,0 Double coordinate is truncated and reduced to six decimal places`() {
        val doubleCoordinate = -0.00000000000000000000000
        Assertions.assertThat(doubleCoordinate.toCoordinate()).isEqualTo(-0.000000)
    }

    @Test
    fun `0 Double coordinate is truncated and reduced to six decimal places`() {
        val doubleCoordinate = 0.1234567891011121314
        Assertions.assertThat(doubleCoordinate.toCoordinate()).isEqualTo(0.123457)
    }

    @Test
    fun `1024 Double coordinate is truncated and reduced to six decimal places`() {
        val doubleCoordinate = 1024.1234567891011121314
        Assertions.assertThat(doubleCoordinate.toCoordinate()).isEqualTo(1024.123457)
    }

    @Test
    fun `-0 Double coordinate is truncated and reduced to six decimal places`() {
        val doubleCoordinate = -0.1234567891011121314
        Assertions.assertThat(doubleCoordinate.toCoordinate()).isEqualTo(-0.123457)
    }

    @Test
    fun `Negative 1024 Double coordinate is truncated and reduced to six decimal places`() {
        val doubleCoordinate = -1024.1234567891011121314
        Assertions.assertThat(doubleCoordinate.toCoordinate()).isEqualTo(-1024.123457)
    }
}