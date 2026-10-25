package fi.kroon.vadret.weatherforecast.extension

import fi.kroon.vadret.core.R
import fi.kroon.vadret.weatherforecast.model.ComingDay
import fi.kroon.vadret.weatherforecast.model.ComingHour
import fi.kroon.vadret.weatherforecast.model.Forecast
import fi.kroon.vadret.weatherforecast.model.PlaceForecast
import fi.kroon.vadret.weatherforecast.model.SunTimes
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

private val stockholm: TimeZone = TimeZone.of(zoneId = "Europe/Stockholm")
private const val RANGE_TOLERANCE = 0.0001f
private const val NOVEMBER = 11
private const val KIRUNA_LATITUDE = 67.85
private const val KIRUNA_LONGITUDE = 20.23

private fun localTime(
    month: Int = NOVEMBER,
    day: Int,
    hour: Int
): Instant =
    LocalDate(
        year = 2026,
        month = month,
        day = day
    ).atTime(
        hour = hour,
        minute = 0
    ).toInstant(timeZone = stockholm)

private fun forecast(
    validAt: Instant,
    intervalStartedAt: Instant = validAt - 1.hours,
    airTemperature: Double = 5.0,
    windSpeed: Double = 3.0,
    symbolCode: Int = 6,
    precipitationAmount: Double? = 0.0
): Forecast =
    Forecast(
        validAt = validAt,
        intervalStartedAt = intervalStartedAt,
        airTemperature = airTemperature,
        windFromDirection = 180.0,
        windSpeed = windSpeed,
        relativeHumidity = 80,
        precipitationType = 0,
        symbolCode = symbolCode,
        precipitationAmount = precipitationAmount
    )

class ForecastTest {

    private fun comingDays(
        forecasts: List<Forecast>,
        now: Instant = localTime(
            day = 10,
            hour = 10
        ),
        locale: Locale = Locale.ENGLISH,
        latitude: Double = 59.33,
        longitude: Double = 18.07
    ): List<ComingDay> =
        PlaceForecast(
            placeName = "Stockholm",
            latitude = latitude,
            longitude = longitude,
            forecasts = forecasts
        ).toWeatherForecast(
            now = now,
            timeZone = stockholm,
            locale = locale
        ).comingDays

    @Test
    fun `days are grouped by local date and only the first hour of today is now`() {
        val days: List<ComingDay> = comingDays(
            forecasts = listOf(
                forecast(validAt = localTime(day = 10, hour = 11)),
                forecast(validAt = localTime(day = 10, hour = 12)),
                forecast(validAt = localTime(day = 11, hour = 6)),
                forecast(validAt = localTime(day = 11, hour = 12))
            )
        )

        assertEquals(
            expected = listOf("coming-day-2026-11-10", "coming-day-2026-11-11"),
            actual = days.map { comingDay: ComingDay -> comingDay.key }
        )
        assertEquals(
            expected = listOf(true, false),
            actual = days.map { comingDay: ComingDay -> comingDay.isToday }
        )
        assertEquals(
            expected = listOf(false, false),
            actual = days.map { comingDay: ComingDay -> comingDay.isExpanded }
        )
        assertEquals(
            expected = listOf(listOf(true, false), listOf(false, false)),
            actual = days.map { comingDay: ComingDay ->
                comingDay.hours.map { comingHour: ComingHour -> comingHour.isNow }
            }
        )
        assertEquals(
            expected = listOf("coming-hour-2026-11-10T10:00:00Z", "coming-hour-2026-11-10T11:00:00Z"),
            actual = days.first().hours.map { comingHour: ComingHour -> comingHour.key }
        )
        assertEquals(
            expected = listOf("06", "12"),
            actual = days.last().hours.map { comingHour: ComingHour -> comingHour.timeText }
        )
    }

    @Test
    fun `a trailing day without daytime hours is left out`() {
        val days: List<ComingDay> = comingDays(
            forecasts = listOf(
                forecast(validAt = localTime(day = 10, hour = 12)),
                forecast(validAt = localTime(day = 11, hour = 0))
            )
        )

        assertEquals(
            expected = listOf("coming-day-2026-11-10"),
            actual = days.map { comingDay: ComingDay -> comingDay.key }
        )
    }

    @Test
    fun `no day is left when none is today and none has daytime hours`() {
        val days: List<ComingDay> = comingDays(
            forecasts = listOf(forecast(validAt = localTime(day = 11, hour = 0)))
        )

        assertEquals(expected = emptyList(), actual = days)
    }

    @Test
    fun `today is kept even late in the evening`() {
        val days: List<ComingDay> = comingDays(
            forecasts = listOf(
                forecast(validAt = localTime(day = 10, hour = 22)),
                forecast(validAt = localTime(day = 10, hour = 23)),
                forecast(validAt = localTime(day = 11, hour = 12))
            ),
            now = localTime(
                day = 10,
                hour = 21
            )
        )

        assertEquals(
            expected = listOf(true, false),
            actual = days.map { comingDay: ComingDay -> comingDay.isToday }
        )
    }

    @Test
    fun `precipitation belongs to the day its interval falls in`() {
        val days: List<ComingDay> = comingDays(
            forecasts = listOf(
                forecast(validAt = localTime(day = 10, hour = 12), precipitationAmount = 0.5),
                forecast(validAt = localTime(day = 10, hour = 18), precipitationAmount = 1.0),
                forecast(validAt = localTime(day = 11, hour = 0), precipitationAmount = 0.3),
                forecast(validAt = localTime(day = 11, hour = 6), precipitationAmount = 0.05),
                forecast(validAt = localTime(day = 11, hour = 12), precipitationAmount = 0.0)
            )
        )

        assertEquals(
            expected = listOf("1.8", null),
            actual = days.map { comingDay: ComingDay -> comingDay.precipitationText }
        )
        assertEquals(
            expected = listOf(listOf("0.5", "1.0"), listOf("0.3", null, null)),
            actual = days.map { comingDay: ComingDay ->
                comingDay.hours.map { comingHour: ComingHour -> comingHour.precipitationText }
            }
        )
    }

    @Test
    fun `a long interval that ends after midnight counts for the day it mostly covers`() {
        val days: List<ComingDay> = comingDays(
            forecasts = listOf(
                forecast(
                    validAt = localTime(day = 10, hour = 13),
                    intervalStartedAt = localTime(day = 10, hour = 1),
                    precipitationAmount = 0.4
                ),
                forecast(
                    validAt = localTime(day = 11, hour = 1),
                    intervalStartedAt = localTime(day = 10, hour = 13),
                    precipitationAmount = 2.0
                ),
                forecast(
                    validAt = localTime(day = 11, hour = 13),
                    intervalStartedAt = localTime(day = 11, hour = 1),
                    precipitationAmount = 0.5
                )
            )
        )

        assertEquals(
            expected = listOf("2.4", "0.5"),
            actual = days.map { comingDay: ComingDay -> comingDay.precipitationText }
        )
    }

    @Test
    fun `an unknown amount makes the day total unknown`() {
        val days: List<ComingDay> = comingDays(
            forecasts = listOf(
                forecast(validAt = localTime(day = 10, hour = 12), precipitationAmount = 3.0),
                forecast(validAt = localTime(day = 10, hour = 13), precipitationAmount = null),
                forecast(validAt = localTime(day = 11, hour = 12), precipitationAmount = 1.0)
            )
        )

        assertEquals(
            expected = listOf(null, "1.0"),
            actual = days.map { comingDay: ComingDay -> comingDay.precipitationText }
        )
        assertEquals(
            expected = listOf("3.0", null),
            actual = days.first().hours.map { comingHour: ComingHour -> comingHour.precipitationText }
        )
    }

    @Test
    fun `temperature ranges share one scale across all days`() {
        val days: List<ComingDay> = comingDays(
            forecasts = listOf(
                forecast(validAt = localTime(day = 10, hour = 12), airTemperature = 2.4),
                forecast(validAt = localTime(day = 10, hour = 15), airTemperature = 7.6),
                forecast(validAt = localTime(day = 11, hour = 6), airTemperature = -1.6),
                forecast(validAt = localTime(day = 11, hour = 12), airTemperature = 4.0)
            )
        )

        assertEquals(
            expected = listOf("2" to "8", "-2" to "4"),
            actual = days.map { comingDay: ComingDay ->
                comingDay.lowTemperatureText to comingDay.highTemperatureText
            }
        )
        assertEquals(
            expected = 4.0f / 9.2f,
            actual = days.first().temperatureRangeStart,
            absoluteTolerance = RANGE_TOLERANCE
        )
        assertEquals(
            expected = 1f,
            actual = days.first().temperatureRangeEnd,
            absoluteTolerance = RANGE_TOLERANCE
        )
        assertEquals(
            expected = 0f,
            actual = days.last().temperatureRangeStart,
            absoluteTolerance = RANGE_TOLERANCE
        )
        assertEquals(
            expected = 5.6f / 9.2f,
            actual = days.last().temperatureRangeEnd,
            absoluteTolerance = RANGE_TOLERANCE
        )
    }

    @Test
    fun `an unchanging temperature fills the whole range`() {
        val day: ComingDay = comingDays(
            forecasts = listOf(
                forecast(validAt = localTime(day = 10, hour = 12)),
                forecast(validAt = localTime(day = 10, hour = 13))
            )
        ).single()

        assertEquals(
            expected = 0f to 1f,
            actual = day.temperatureRangeStart to day.temperatureRangeEnd
        )
    }

    @Test
    fun `the day icon is the most common daytime weather`() {
        val day: ComingDay = comingDays(
            forecasts = listOf(
                forecast(validAt = localTime(day = 10, hour = 3), symbolCode = 1),
                forecast(validAt = localTime(day = 10, hour = 4), symbolCode = 1),
                forecast(validAt = localTime(day = 10, hour = 6), symbolCode = 6),
                forecast(validAt = localTime(day = 10, hour = 12), symbolCode = 3),
                forecast(validAt = localTime(day = 10, hour = 18), symbolCode = 3)
            )
        ).single()

        assertEquals(expected = R.drawable.ic_partly_cloudy_day, actual = day.iconResourceId)
    }

    @Test
    fun `a tie in daytime weather goes to the worse weather`() {
        val day: ComingDay = comingDays(
            forecasts = listOf(
                forecast(validAt = localTime(day = 10, hour = 9), symbolCode = 3),
                forecast(validAt = localTime(day = 10, hour = 12), symbolCode = 6)
            )
        ).single()

        assertEquals(expected = R.drawable.ic_cloud, actual = day.iconResourceId)
        assertEquals(expected = R.string.wsymb2_overcast, actual = day.descriptionResourceId)
    }

    @Test
    fun `a late evening today takes its icon from the hours that are left`() {
        val today: ComingDay = comingDays(
            forecasts = listOf(
                forecast(validAt = localTime(day = 10, hour = 20), symbolCode = 3),
                forecast(validAt = localTime(day = 10, hour = 21), symbolCode = 3),
                forecast(validAt = localTime(day = 10, hour = 22), symbolCode = 6)
            ),
            now = localTime(
                day = 10,
                hour = 19
            )
        ).single()

        assertEquals(expected = R.drawable.ic_partly_cloudy_day, actual = today.iconResourceId)
    }

    @Test
    fun `hours show the moon at night and the sun by day`() {
        val day: ComingDay = comingDays(
            forecasts = listOf(
                forecast(validAt = localTime(day = 10, hour = 3), symbolCode = 1),
                forecast(validAt = localTime(day = 10, hour = 12), symbolCode = 1)
            )
        ).single()

        assertEquals(
            expected = listOf(R.drawable.ic_clear_night, R.drawable.ic_clear_day),
            actual = day.hours.map { comingHour: ComingHour -> comingHour.iconResourceId }
        )
    }

    @Test
    fun `wind is the strongest of the day and the sun rises and sets`() {
        val day: ComingDay = comingDays(
            forecasts = listOf(
                forecast(validAt = localTime(day = 10, hour = 9), windSpeed = 4.2),
                forecast(validAt = localTime(day = 10, hour = 12), windSpeed = 7.8)
            )
        ).single()

        assertEquals(expected = "7.8", actual = day.windSpeedText)
        val sunTimes: SunTimes.RiseAndSet = assertIs<SunTimes.RiseAndSet>(value = day.sunTimes)
        assertTrue(actual = sunTimes.sunriseText.isNotBlank())
        assertTrue(actual = sunTimes.sunsetText.isNotBlank())
    }

    @Test
    fun `the sun stays down during polar night in kiruna`() {
        val day: ComingDay = comingDays(
            forecasts = listOf(forecast(validAt = localTime(month = 12, day = 21, hour = 12))),
            now = localTime(
                month = 12,
                day = 21,
                hour = 10
            ),
            latitude = KIRUNA_LATITUDE,
            longitude = KIRUNA_LONGITUDE
        ).single()

        assertEquals(expected = SunTimes.PolarNight, actual = day.sunTimes)
    }

    @Test
    fun `the sun stays up during midnight sun in kiruna`() {
        val day: ComingDay = comingDays(
            forecasts = listOf(forecast(validAt = localTime(month = 6, day = 21, hour = 12))),
            now = localTime(
                month = 6,
                day = 21,
                hour = 10
            ),
            latitude = KIRUNA_LATITUDE,
            longitude = KIRUNA_LONGITUDE
        ).single()

        assertEquals(expected = SunTimes.MidnightSun, actual = day.sunTimes)
    }

    @Test
    fun `every weekday gets a short name and date`() {
        val days: List<ComingDay> = comingDays(
            forecasts = (16 until 16 + DayOfWeek.entries.size).map { day: Int ->
                forecast(validAt = localTime(day = day, hour = 12))
            }
        )

        assertEquals(
            expected = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
            actual = days.map { comingDay: ComingDay -> comingDay.weekdayText }
        )
        assertEquals(
            expected = (16..22).map { day: Int -> "$day Nov" },
            actual = days.map { comingDay: ComingDay -> comingDay.dateText }
        )
    }

    @Test
    fun `swedish short weekdays start with a capital letter`() {
        val day: ComingDay = comingDays(
            forecasts = listOf(forecast(validAt = localTime(day = 22, hour = 12))),
            locale = Locale.forLanguageTag("sv-SE")
        ).single()

        assertEquals(expected = "Sön", actual = day.weekdayText)
    }
}