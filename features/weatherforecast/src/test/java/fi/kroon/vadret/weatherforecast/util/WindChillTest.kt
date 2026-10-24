package fi.kroon.vadret.weatherforecast.util

import java.util.Locale
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class WindChillTest {

    private val defaultLocale: Locale = Locale.getDefault()

    @AfterTest
    fun tearDown() {
        Locale.setDefault(defaultLocale)
    }

    @Test
    fun `wind makes the cold feel colder`() {
        assertEquals(
            expected = listOf("-14.1", "-33.6"),
            actual = listOf(
                WindChill.calculate(temperatureCelcius = -10.0, windSpeedMeterPerSecond = 2.0),
                WindChill.calculate(temperatureCelcius = -20.0, windSpeedMeterPerSecond = 10.0)
            )
        )
    }

    @Test
    fun `the edges of the formula are included`() {
        assertEquals(
            expected = listOf("-49.1", "9.2"),
            actual = listOf(
                WindChill.calculate(temperatureCelcius = -40.0, windSpeedMeterPerSecond = 2.0),
                WindChill.calculate(temperatureCelcius = 10.0, windSpeedMeterPerSecond = 2.0)
            )
        )
    }

    @Test
    fun `outside the formula there is no wind chill`() {
        assertEquals(
            expected = listOf(null, null, null, null),
            actual = listOf(
                WindChill.calculate(temperatureCelcius = -10.0, windSpeedMeterPerSecond = 1.9),
                WindChill.calculate(temperatureCelcius = -10.0, windSpeedMeterPerSecond = 35.1),
                WindChill.calculate(temperatureCelcius = 10.1, windSpeedMeterPerSecond = 5.0),
                WindChill.calculate(temperatureCelcius = -40.1, windSpeedMeterPerSecond = 5.0)
            )
        )
    }

    @Test
    fun `the decimal point does not follow the locale`() {
        Locale.setDefault(Locale.forLanguageTag("sv-SE"))

        assertEquals(
            expected = "-14.1",
            actual = WindChill.calculate(temperatureCelcius = -10.0, windSpeedMeterPerSecond = 2.0)
        )
    }
}