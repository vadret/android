package fi.kroon.vadret.weatherforecast.extension

import fi.kroon.vadret.weatherforecast.model.Forecast
import fi.kroon.vadret.weatherforecast.model.PlaceForecast
import fi.kroon.vadret.weatherforecast.model.WeatherForecast
import fi.kroon.vadret.weatherforecast.model.WeatherForecastRow
import fi.kroon.vadret.weatherforecast.util.SunriseSunset
import fi.kroon.vadret.weatherforecast.util.SunsetUtil
import fi.kroon.vadret.weatherforecast.util.WeatherForecastUtil
import fi.kroon.vadret.weatherforecast.util.WindChill
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Instant
import java.util.TimeZone as JavaTimeZone

private const val WEEKDAY_PATTERN = "EEEE"
private const val DAY_KEY_PREFIX = "day-"
private const val HOUR_KEY_PREFIX = "hour-"

/**
 *  The wind arrow drawable points east.
 */
private const val WIND_ARROW_ROTATION_OFFSET = 90f
private const val NO_PRECIPITATION = 0

private val hourFormat = LocalTime.Format {
    hour()
    char(value = ':')
    minute()
}

/**
 *  The overview shows the first forecast, the days list every forecast
 *  grouped by date in [timeZone]. SMHI's times are in UTC.
 */
internal fun PlaceForecast.toWeatherForecast(
    now: Instant,
    timeZone: TimeZone,
    locale: Locale
): WeatherForecast {
    val first: Forecast = forecasts.firstOrNull() ?: return WeatherForecast(
        overview = emptyList(),
        days = emptyList()
    )
    val javaTimeZone: JavaTimeZone = JavaTimeZone.getTimeZone(timeZone.id)
    val dateFormat: DateFormat = DateFormat.getDateInstance(DateFormat.LONG, locale)
    val timeFormat: DateFormat = DateFormat.getTimeInstance(DateFormat.SHORT, locale)
    val weekdayFormat: DateFormat = SimpleDateFormat(WEEKDAY_PATTERN, locale)
    listOf(dateFormat, timeFormat, weekdayFormat).forEach { format: DateFormat ->
        format.timeZone = javaTimeZone
    }
    return WeatherForecast(
        overview = listOf(
            first.toHeadline(),
            first.toSummary(
                sunriseSunset = SunsetUtil.getSunriseSunset(
                    day = now,
                    latitude = latitude,
                    longitude = longitude
                ),
                timeFormat = timeFormat
            )
        ),
        days = forecasts.toDayRows(
            timeZone = timeZone,
            locale = locale,
            dateFormat = dateFormat,
            weekdayFormat = weekdayFormat
        )
    )
}

private fun List<Forecast>.toDayRows(
    timeZone: TimeZone,
    locale: Locale,
    dateFormat: DateFormat,
    weekdayFormat: DateFormat
): List<WeatherForecastRow> =
    groupBy { forecast: Forecast -> forecast.validAt.toLocalDateTime(timeZone = timeZone).date }
        .flatMap { (date: LocalDate, dayForecasts: List<Forecast>) ->
            val startOfDay = Date(date.atStartOfDayIn(timeZone = timeZone).toEpochMilliseconds())
            val day = WeatherForecastRow.Day(
                key = "$DAY_KEY_PREFIX$date",
                weekday = weekdayFormat.format(startOfDay).uppercase(locale = locale),
                dateText = dateFormat.format(startOfDay)
            )
            listOf(day) + dayForecasts.map { forecast: Forecast -> forecast.toHour(timeZone = timeZone) }
        }

private fun Forecast.toHeadline(): WeatherForecastRow.Headline =
    WeatherForecastRow.Headline(
        descriptionResourceId = WeatherForecastUtil.getWsymb2ResourceId(index = symbolCode),
        windSpeedClassResourceId = WeatherForecastUtil.getWindSpeedClassResourceId(windSpeed = windSpeed),
        windDirectionResourceId = WeatherForecastUtil.handleWindDirection(degree = windFromDirection),
        windArrowRotation = windFromDirection.toFloat() + WIND_ARROW_ROTATION_OFFSET
    )

private fun Forecast.toSummary(
    sunriseSunset: SunriseSunset?,
    timeFormat: DateFormat
): WeatherForecastRow.Summary =
    WeatherForecastRow.Summary(
        sunriseText = if (sunriseSunset != null) {
            timeFormat.format(Date(sunriseSunset.sunrise.toEpochMilliseconds()))
        } else {
            null
        },
        sunsetText = if (sunriseSunset != null) {
            timeFormat.format(Date(sunriseSunset.sunset.toEpochMilliseconds()))
        } else {
            null
        },
        temperature = airTemperature,
        feelsLikeTemperature = WindChill.calculate(
            temperatureCelcius = airTemperature,
            windSpeedMeterPerSecond = windSpeed
        ),
        humidityPercent = relativeHumidity,
        windSpeed = windSpeed,
        precipitationResourceId = if (precipitationType > NO_PRECIPITATION) {
            WeatherForecastUtil.getPrecipitationResourceId(prSort = precipitationType)
        } else {
            null
        }
    )

private fun Forecast.toHour(timeZone: TimeZone): WeatherForecastRow.Hour =
    WeatherForecastRow.Hour(
        key = "$HOUR_KEY_PREFIX$validAt",
        timeText = validAt
            .toLocalDateTime(timeZone = timeZone)
            .time
            .format(format = hourFormat),
        temperature = airTemperature,
        feelsLikeTemperature = WindChill.calculate(
            temperatureCelcius = airTemperature,
            windSpeedMeterPerSecond = windSpeed
        ),
        descriptionResourceId = WeatherForecastUtil.getWsymb2ResourceId(index = symbolCode),
        iconResourceId = WeatherForecastUtil.getWsymb2IconResourceId(index = symbolCode),
        temperatureColorResourceId = WeatherForecastUtil.getTemperatureColorResourceId(temperature = airTemperature)
    )