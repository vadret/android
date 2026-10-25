package fi.kroon.vadret.weatherforecast.extension

import fi.kroon.vadret.weatherforecast.comingDay
import fi.kroon.vadret.weatherforecast.model.ComingDay
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ComingDayTest {

    private val monday: ComingDay = comingDay(dayKey = "coming-day-2026-11-16")
    private val tuesday: ComingDay = comingDay(dayKey = "coming-day-2026-11-17")

    private fun List<ComingDay>.expansion(): List<Boolean> = map { comingDay: ComingDay -> comingDay.isExpanded }

    @Test
    fun `a toggled day opens alone and closes again`() {
        val opened: List<ComingDay> = listOf(monday, tuesday).withDayToggled(dayKey = tuesday.key)

        assertEquals(expected = listOf(false, true), actual = opened.expansion())
        assertEquals(
            expected = listOf(false, false),
            actual = opened.withDayToggled(dayKey = tuesday.key).expansion()
        )
    }

    @Test
    fun `toggling a day that is not shown changes nothing`() {
        assertEquals(
            expected = listOf(monday, tuesday),
            actual = listOf(monday, tuesday).withDayToggled(dayKey = "coming-day-2026-11-18")
        )
    }

    @Test
    fun `every day opens and closes at once`() {
        val opened: List<ComingDay> = listOf(monday, tuesday).withEveryDayExpanded(isExpanded = true)

        assertEquals(expected = listOf(true, true), actual = opened.expansion())
        assertEquals(
            expected = listOf(false, false),
            actual = opened.withEveryDayExpanded(isExpanded = false).expansion()
        )
    }

    @Test
    fun `open days stay open in a new forecast and new days start closed`() {
        val previousComingDays: List<ComingDay> = listOf(
            comingDay(
                dayKey = "coming-day-2026-11-15",
                isExpanded = true
            ),
            monday.copy(isExpanded = true),
            tuesday
        )

        val comingDays: List<ComingDay> = listOf(
            monday,
            tuesday.copy(isExpanded = true),
            comingDay(dayKey = "coming-day-2026-11-18")
        ).withExpansionOf(previousComingDays = previousComingDays)

        assertEquals(expected = listOf(true, false, false), actual = comingDays.expansion())
    }

    @Test
    fun `every day is expanded only when there are days and all are open`() {
        assertFalse(actual = emptyList<ComingDay>().isEveryDayExpanded())
        assertFalse(actual = listOf(monday.copy(isExpanded = true), tuesday).isEveryDayExpanded())
        assertTrue(actual = listOf(monday, tuesday).withEveryDayExpanded(isExpanded = true).isEveryDayExpanded())
    }
}