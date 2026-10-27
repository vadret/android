package fi.kroon.vadret.weatherforecast.extension

import kotlin.test.Test
import kotlin.test.assertEquals

class DoubleTest {

    @Test
    fun `a whole degree has no trailing zero`() {
        assertEquals(
            expected = listOf("11", "-10", "0", "11"),
            actual = listOf(11.0, -10.0, 0.0, 10.96).map { temperature: Double -> temperature.toTemperatureText() }
        )
    }

    @Test
    fun `a fraction of a degree keeps one decimal`() {
        assertEquals(
            expected = listOf("10.9", "-0.5", "3.1", "-14.1"),
            actual = listOf(10.9, -0.5, 3.14, -14.1).map { temperature: Double -> temperature.toTemperatureText() }
        )
    }

    @Test
    fun `just below zero is not shown as minus zero`() {
        assertEquals(expected = "0", actual = (-0.04).toTemperatureText())
    }

    @Test
    fun `one decimal is always kept with a dot`() {
        assertEquals(
            expected = listOf("5.0", "0.1", "12.3", "-3.0"),
            actual = listOf(5.0, 0.1, 12.34, -3.0).map { value: Double -> value.toOneDecimalText() }
        )
    }

    @Test
    fun `one decimal rounds half away from zero`() {
        assertEquals(
            expected = listOf("2.3", "-2.3", "0.2"),
            actual = listOf(2.25, -2.25, 0.15).map { value: Double -> value.toOneDecimalText() }
        )
    }

    @Test
    fun `a value that rounds to zero has no sign`() {
        assertEquals(expected = "0.0", actual = (-0.04).toOneDecimalText())
    }
}