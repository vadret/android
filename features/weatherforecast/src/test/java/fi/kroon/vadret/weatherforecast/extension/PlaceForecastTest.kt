package fi.kroon.vadret.weatherforecast.extension

import fi.kroon.vadret.core.R
import fi.kroon.vadret.weatherforecast.model.Forecast
import fi.kroon.vadret.weatherforecast.model.PlaceForecast
import fi.kroon.vadret.weatherforecast.model.WeatherForecast
import fi.kroon.vadret.weatherforecast.model.WeatherForecastRow
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Instant

private val stockholm: TimeZone = TimeZone.of(zoneId = "Europe/Stockholm")

private fun forecast(validAt: Instant): Forecast =
    Forecast(
        validAt = validAt,
        airTemperature = 10.9,
        windFromDirection = 160.0,
        windSpeed = 5.4,
        relativeHumidity = 86,
        precipitationType = 0,
        symbolCode = 6
    )

class PlaceForecastTest {

    private fun rows(
        validAt: List<Instant>,
        now: Instant = Instant.parse(input = "2026-10-24T12:00:00Z")
    ): List<WeatherForecastRow> {
        val weatherForecast: WeatherForecast = PlaceForecast(
            placeName = "Stockholm",
            latitude = 59.33,
            longitude = 18.07,
            forecasts = validAt.map { instant: Instant -> forecast(validAt = instant) }
        ).toWeatherForecast(
            now = now,
            timeZone = stockholm,
            locale = Locale.ENGLISH
        )
        return weatherForecast.overview + weatherForecast.days
    }

    @Test
    fun `hours are grouped by local date`() {
        val rows: List<WeatherForecastRow> = rows(
            validAt = listOf(
                Instant.parse(input = "2026-10-24T20:00:00Z"),
                Instant.parse(input = "2026-10-24T21:00:00Z"),
                Instant.parse(input = "2026-10-24T22:00:00Z")
            )
        )

        assertEquals(
            expected = listOf(
                "headline",
                "summary",
                "day-2026-10-24",
                "hour-2026-10-24T20:00:00Z",
                "hour-2026-10-24T21:00:00Z",
                "day-2026-10-25",
                "hour-2026-10-24T22:00:00Z"
            ),
            actual = rows.map { row: WeatherForecastRow -> row.key }
        )
        val midnight: WeatherForecastRow = rows.last()
        assertIs<WeatherForecastRow.Hour>(value = midnight)
        assertEquals(expected = "00:00", actual = midnight.timeText)
    }

    @Test
    fun `every weekday gets its own localised header`() {
        val monday = LocalDate(year = 2026, month = 10, day = 19)
        val days: List<LocalDate> = (0 until DayOfWeek.entries.size).map { offset: Int -> monday.plus(period = DatePeriod(days = offset)) }

        val headers: List<WeatherForecastRow.Day> = rows(
            validAt = days.map { date: LocalDate -> date.atTime(hour = 12, minute = 0).toInstant(timeZone = stockholm) }
        ).filterIsInstance<WeatherForecastRow.Day>()

        assertEquals(
            expected = listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"),
            actual = headers.map { day: WeatherForecastRow.Day -> day.weekday }
        )
        assertEquals(
            expected = (19..25).map { day: Int -> "October $day, 2026" },
            actual = headers.map { day: WeatherForecastRow.Day -> day.dateText }
        )
    }

    @Test
    fun `headline and summary describe the first forecast`() {
        val rows: List<WeatherForecastRow> = rows(validAt = listOf(Instant.parse(input = "2026-10-24T12:00:00Z")))
        val headline: WeatherForecastRow = rows[0]
        val summary: WeatherForecastRow = rows[1]

        assertIs<WeatherForecastRow.Headline>(value = headline)
        assertEquals(expected = R.string.wsymb2_overcast, actual = headline.descriptionResourceId)
        assertEquals(expected = 250f, actual = headline.windArrowRotation)
        assertIs<WeatherForecastRow.Summary>(value = summary)
        assertEquals(expected = 86, actual = summary.humidityPercent)
        assertEquals(expected = null, actual = summary.precipitationResourceId)
    }

    @Test
    fun `no forecasts give no rows`() {
        assertEquals(expected = emptyList(), actual = rows(validAt = emptyList()))
    }
}