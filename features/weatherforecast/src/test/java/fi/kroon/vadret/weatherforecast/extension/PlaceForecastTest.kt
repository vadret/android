package fi.kroon.vadret.weatherforecast.extension

import fi.kroon.vadret.core.R
import fi.kroon.vadret.weatherforecast.model.CurrentWeather
import fi.kroon.vadret.weatherforecast.model.Forecast
import fi.kroon.vadret.weatherforecast.model.ForecastDay
import fi.kroon.vadret.weatherforecast.model.ForecastHour
import fi.kroon.vadret.weatherforecast.model.PlaceForecast
import fi.kroon.vadret.weatherforecast.model.WeatherForecast
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Instant

private val stockholm: TimeZone = TimeZone.of(zoneId = "Europe/Stockholm")

private fun forecast(
    validAt: Instant,
    symbolCode: Int = 6,
    precipitationType: Int = 0,
    windFromDirection: Double = 160.0
): Forecast =
    Forecast(
        validAt = validAt,
        airTemperature = 10.9,
        windFromDirection = windFromDirection,
        windSpeed = 5.4,
        relativeHumidity = 86,
        precipitationType = precipitationType,
        symbolCode = symbolCode
    )

class PlaceForecastTest {

    private fun weatherForecast(
        forecasts: List<Forecast>,
        locale: Locale = Locale.ENGLISH,
        now: Instant = Instant.parse(input = "2026-10-24T12:00:00Z")
    ): WeatherForecast =
        PlaceForecast(
            placeName = "Stockholm",
            latitude = 59.33,
            longitude = 18.07,
            forecasts = forecasts
        ).toWeatherForecast(
            now = now,
            timeZone = stockholm,
            locale = locale
        )

    private fun currentWeather(forecast: Forecast): CurrentWeather {
        val currentWeather: CurrentWeather? = weatherForecast(forecasts = listOf(forecast)).current
        assertNotNull(actual = currentWeather)
        return currentWeather
    }

    @Test
    fun `hours are grouped by local date`() {
        val days: List<ForecastDay> = weatherForecast(
            forecasts = listOf(
                forecast(validAt = Instant.parse(input = "2026-10-24T20:00:00Z")),
                forecast(validAt = Instant.parse(input = "2026-10-24T21:00:00Z")),
                forecast(validAt = Instant.parse(input = "2026-10-24T22:00:00Z"))
            )
        ).days

        assertEquals(
            expected = listOf("day-2026-10-24", "day-2026-10-25"),
            actual = days.map { day: ForecastDay -> day.key }
        )
        assertEquals(
            expected = listOf(
                listOf("hour-2026-10-24T20:00:00Z", "hour-2026-10-24T21:00:00Z"),
                listOf("hour-2026-10-24T22:00:00Z")
            ),
            actual = days.map { day: ForecastDay -> day.hours.map { hour: ForecastHour -> hour.key } }
        )
        assertEquals(expected = "00:00", actual = days.last().hours.single().timeText)
    }

    @Test
    fun `every weekday gets its own capitalised header`() {
        val monday = LocalDate(year = 2026, month = 10, day = 19)
        val dates: List<LocalDate> = (0 until DayOfWeek.entries.size).map { offset: Int -> monday.plus(period = DatePeriod(days = offset)) }

        val days: List<ForecastDay> = weatherForecast(
            forecasts = dates.map { date: LocalDate -> forecast(validAt = date.atTime(hour = 12, minute = 0).toInstant(timeZone = stockholm)) }
        ).days

        assertEquals(
            expected = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"),
            actual = days.map { day: ForecastDay -> day.weekday }
        )
        assertEquals(
            expected = (19..25).map { day: Int -> "October $day, 2026" },
            actual = days.map { day: ForecastDay -> day.dateText }
        )
    }

    @Test
    fun `swedish weekdays start with a capital letter`() {
        val days: List<ForecastDay> = weatherForecast(
            forecasts = listOf(forecast(validAt = Instant.parse(input = "2026-10-25T12:00:00Z"))),
            locale = Locale.forLanguageTag("sv-SE")
        ).days

        assertEquals(expected = "Söndag", actual = days.single().weekday)
    }

    @Test
    fun `the current weather describes the first forecast`() {
        val currentWeather: CurrentWeather = currentWeather(forecast = forecast(validAt = Instant.parse(input = "2026-10-24T12:00:00Z")))

        assertEquals(expected = 10.9, actual = currentWeather.temperature)
        assertEquals(expected = R.string.wsymb2_overcast, actual = currentWeather.descriptionResourceId)
        assertEquals(expected = R.drawable.ic_cloud, actual = currentWeather.iconResourceId)
        assertEquals(expected = 86, actual = currentWeather.humidityPercent)
        assertEquals(expected = 5.4, actual = currentWeather.windSpeed)
        assertEquals(expected = R.string.precipitation_none, actual = currentWeather.precipitationResourceId)
        assertNotNull(actual = currentWeather.sunriseText)
        assertNotNull(actual = currentWeather.sunsetText)
    }

    @Test
    fun `the wind arrow points where the wind blows`() {
        assertEquals(
            expected = 340f,
            actual = currentWeather(forecast = forecast(validAt = Instant.parse(input = "2026-10-24T12:00:00Z"))).windArrowRotation
        )
        assertEquals(
            expected = 112f,
            actual = currentWeather(
                forecast = forecast(
                    validAt = Instant.parse(input = "2026-10-24T12:00:00Z"),
                    windFromDirection = 292.0
                )
            ).windArrowRotation
        )
    }

    @Test
    fun `precipitation is named by type`() {
        assertEquals(
            expected = R.string.prsort_snow,
            actual = currentWeather(
                forecast = forecast(
                    validAt = Instant.parse(input = "2026-10-24T12:00:00Z"),
                    precipitationType = 5
                )
            ).precipitationResourceId
        )
    }

    @Test
    fun `clear sky shows the sun by day and the moon by night`() {
        val hours: List<ForecastHour> = weatherForecast(
            forecasts = listOf(
                forecast(validAt = Instant.parse(input = "2026-10-24T10:00:00Z"), symbolCode = 1),
                forecast(validAt = Instant.parse(input = "2026-10-24T20:00:00Z"), symbolCode = 1),
                forecast(validAt = Instant.parse(input = "2026-10-24T10:00:00Z"), symbolCode = 3),
                forecast(validAt = Instant.parse(input = "2026-10-24T20:00:00Z"), symbolCode = 3)
            )
        ).days.flatMap { day: ForecastDay -> day.hours }

        assertEquals(
            expected = listOf(
                R.drawable.ic_clear_day,
                R.drawable.ic_clear_night,
                R.drawable.ic_partly_cloudy_day,
                R.drawable.ic_partly_cloudy_night
            ),
            actual = hours.map { hour: ForecastHour -> hour.iconResourceId }
        )
    }

    @Test
    fun `cloud and precipitation symbols look the same by day and night`() {
        val symbolCodes: List<Int> = (5..27).toList()

        val dayIcons: List<Int> = weatherForecast(
            forecasts = symbolCodes.map { symbolCode: Int ->
                forecast(
                    validAt = Instant.parse(input = "2026-10-24T10:00:00Z"),
                    symbolCode = symbolCode
                )
            }
        ).days.flatMap { day: ForecastDay -> day.hours.map { hour: ForecastHour -> hour.iconResourceId } }
        val nightIcons: List<Int> = weatherForecast(
            forecasts = symbolCodes.map { symbolCode: Int ->
                forecast(
                    validAt = Instant.parse(input = "2026-10-24T20:00:00Z"),
                    symbolCode = symbolCode
                )
            }
        ).days.flatMap { day: ForecastDay -> day.hours.map { hour: ForecastHour -> hour.iconResourceId } }

        assertEquals(expected = dayIcons, actual = nightIcons)
    }

    @Test
    fun `no forecasts give no weather`() {
        val weatherForecast: WeatherForecast = weatherForecast(forecasts = emptyList())

        assertNull(actual = weatherForecast.current)
        assertEquals(expected = emptyList(), actual = weatherForecast.days)
    }
}