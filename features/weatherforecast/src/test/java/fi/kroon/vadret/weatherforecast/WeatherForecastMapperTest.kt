package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.core.R
import fi.kroon.vadret.data.location.model.Location
import fi.kroon.vadret.data.weatherforecast.model.TimeSerie
import fi.kroon.vadret.data.weatherforecast.model.WeatherData
import fi.kroon.vadret.weatherforecast.model.WeatherForecast
import fi.kroon.vadret.weatherforecast.model.WeatherForecastRow
import kotlinx.datetime.TimeZone
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Instant

class WeatherForecastMapperTest {

    private val stockholm: TimeZone = TimeZone.of(zoneId = "Europe/Stockholm")

    private fun map(times: List<String>): List<WeatherForecastRow> {
        val forecast: WeatherForecast = WeatherForecastMapper(
            timeSeries = times.map { time: String -> timeSerie(time = time) },
            location = Location(
                latitude = 59.33,
                longitude = 18.07
            ),
            now = Instant.parse(input = "2026-10-24T12:00:00Z"),
            timeZone = stockholm,
            locale = Locale.ENGLISH
        )
        return forecast.overview + forecast.days
    }

    @Test
    fun `hours are grouped by local date`() {
        val rows: List<WeatherForecastRow> = map(
            times = listOf(
                "2026-10-24T20:00:00Z",
                "2026-10-24T21:00:00Z",
                "2026-10-24T22:00:00Z"
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
        assertEquals(
            expected = "00:00",
            actual = midnight.timeText
        )
    }

    @Test
    fun `day header is localised`() {
        val day: WeatherForecastRow = map(times = listOf("2026-10-24T12:00:00Z"))[2]

        assertIs<WeatherForecastRow.Day>(value = day)
        assertEquals(
            expected = "SATURDAY",
            actual = day.weekday
        )
        assertEquals(
            expected = "October 24, 2026",
            actual = day.dateText
        )
    }

    @Test
    fun `headline and summary describe the first hour`() {
        val rows: List<WeatherForecastRow> = map(times = listOf("2026-10-24T12:00:00Z"))
        val headline: WeatherForecastRow = rows[0]
        val summary: WeatherForecastRow = rows[1]

        assertIs<WeatherForecastRow.Headline>(value = headline)
        assertEquals(
            expected = R.string.wsymb2_overcast,
            actual = headline.descriptionResourceId
        )
        assertIs<WeatherForecastRow.Summary>(value = summary)
        assertEquals(
            expected = 86,
            actual = summary.humidityPercent
        )
    }

    @Test
    fun `no time series gives no rows`() {
        assertEquals(
            expected = emptyList(),
            actual = map(times = emptyList())
        )
    }

    private fun timeSerie(time: String): TimeSerie =
        TimeSerie(
            time = time,
            data = WeatherData(
                airTemperature = 10.9,
                windFromDirection = 160.0,
                windSpeed = 5.4,
                relativeHumidity = 86,
                thunderstormProbability = 0,
                precipitationType = 0,
                probabilityOfPrecipitation = 0,
                precipitationAmountMin = 0.0,
                precipitationAmountMax = 0.0,
                symbolCode = 6
            )
        )
}