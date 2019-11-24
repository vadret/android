package fi.kroon.vadret.common

import fi.kroon.vadret.util.extension.toCoordinate
import junit.framework.Assert.assertEquals
import org.junit.Test

class CoordinateTest {

    @Test
    fun `handle invalid double string format`() {

        val latBad = "−87.684700"
        val lon = "-8.12"

        val latDouble: Double = latBad.toCoordinate()
        val lonDouble: Double = lon.toCoordinate()

        assertEquals(latDouble, -87.684700)
        assertEquals(lonDouble, -8.12)
    }

}