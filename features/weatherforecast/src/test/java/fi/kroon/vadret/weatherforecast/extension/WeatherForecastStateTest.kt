package fi.kroon.vadret.weatherforecast.extension

import fi.kroon.vadret.weatherforecast.WeatherForecastState
import fi.kroon.vadret.weatherforecast.comingDay
import fi.kroon.vadret.weatherforecast.model.ComingDay
import fi.kroon.vadret.weatherforecast.model.WeatherForecast
import kotlinx.collections.immutable.persistentListOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WeatherForecastStateTest {

    private val openState: WeatherForecastState = WeatherForecastState().withComingDays(
        comingDays = persistentListOf(
            comingDay(
                dayKey = "coming-day-2026-11-16",
                isExpanded = true
            )
        )
    )
    private val newForecast = WeatherForecast(
        comingDays = persistentListOf(
            comingDay(dayKey = "coming-day-2026-11-16"),
            comingDay(dayKey = "coming-day-2026-11-17")
        )
    )

    private fun WeatherForecastState.expansion(): List<Boolean> =
        forecast.comingDays.map { comingDay: ComingDay ->
            comingDay.isExpanded
        }

    @Test
    fun `new coming days also decide whether every day is expanded`() {
        assertTrue(actual = openState.isEveryDayExpanded)
        assertFalse(
            actual = openState
                .withComingDays(comingDays = persistentListOf(comingDay(dayKey = "coming-day-2026-11-16")))
                .isEveryDayExpanded
        )
    }

    @Test
    fun `a new forecast for the same place keeps the open days`() {
        val state: WeatherForecastState = openState.withForecast(
            forecast = newForecast,
            isSamePlace = true
        )

        assertEquals(expected = listOf(true, false), actual = state.expansion())
        assertFalse(actual = state.isEveryDayExpanded)
    }

    @Test
    fun `a forecast for another place starts with every day closed`() {
        val state: WeatherForecastState = openState.withForecast(
            forecast = newForecast,
            isSamePlace = false
        )

        assertEquals(expected = listOf(false, false), actual = state.expansion())
        assertFalse(actual = state.isEveryDayExpanded)
    }
}