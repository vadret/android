package fi.kroon.vadret.weatherforecast.extension

import fi.kroon.vadret.weatherforecast.model.CurrentWeather
import fi.kroon.vadret.weatherforecast.model.Forecast
import fi.kroon.vadret.weatherforecast.model.ForecastDay
import fi.kroon.vadret.weatherforecast.model.ForecastHour
import fi.kroon.vadret.weatherforecast.model.PlaceForecast
import fi.kroon.vadret.weatherforecast.model.WeatherForecast
import fi.kroon.vadret.weatherforecast.util.SunsetUtil
import fi.kroon.vadret.weatherforecast.util.WeatherForecastUtil
import fi.kroon.vadret.weatherforecast.util.WindChill
import kotlinx.collections.immutable.toImmutableList
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

private const val WIND_ARROW_ROTATION_OFFSET = 180f
private const val DEGREES_IN_CIRCLE = 360f

private val hourFormat = LocalTime.Format {
    hour()
    char(value = ':')
    minute()
}

internal fun PlaceForecast.toWeatherForecast(
    now: Instant,
    timeZone: TimeZone,
    locale: Locale
): WeatherForecast {
    val first: Forecast = forecasts.firstOrNull() ?: return WeatherForecast()
    val javaTimeZone: JavaTimeZone = JavaTimeZone.getTimeZone(timeZone.id)
    val dateFormat: DateFormat = DateFormat.getDateInstance(DateFormat.LONG, locale)
    val timeFormat: DateFormat = DateFormat.getTimeInstance(DateFormat.SHORT, locale)
    val weekdayFormat: DateFormat = SimpleDateFormat(WEEKDAY_PATTERN, locale)
    listOf(dateFormat, timeFormat, weekdayFormat).forEach { format: DateFormat ->
        format.timeZone = javaTimeZone
    }
    return WeatherForecast(
        current = first.toCurrentWeather(isDaytime = isDaytime(at = first.validAt)),
        comingDays = forecasts.toComingDays(
            now = now,
            timeZone = timeZone,
            locale = locale,
            latitude = latitude,
            longitude = longitude,
            timeFormat = timeFormat
        ).toImmutableList(),
        days = toForecastDays(
            timeZone = timeZone,
            locale = locale,
            dateFormat = dateFormat,
            weekdayFormat = weekdayFormat
        ).toImmutableList()
    )
}

private fun PlaceForecast.isDaytime(at: Instant): Boolean =
    SunsetUtil.isSunUp(
        at = at,
        latitude = latitude,
        longitude = longitude
    )

private fun PlaceForecast.toForecastDays(
    timeZone: TimeZone,
    locale: Locale,
    dateFormat: DateFormat,
    weekdayFormat: DateFormat
): List<ForecastDay> =
    forecasts
        .groupBy { forecast: Forecast -> forecast.validAt.toLocalDateTime(timeZone = timeZone).date }
        .map { (date: LocalDate, dayForecasts: List<Forecast>) ->
            val startOfDay = Date(date.atStartOfDayIn(timeZone = timeZone).toEpochMilliseconds())
            ForecastDay(
                key = "$DAY_KEY_PREFIX$date",
                weekday = weekdayFormat
                    .format(startOfDay)
                    .replaceFirstChar { character: Char -> character.titlecase(locale = locale) },
                dateText = dateFormat.format(startOfDay),
                hours = dayForecasts
                    .map { forecast: Forecast ->
                        forecast.toForecastHour(
                            timeZone = timeZone,
                            isDaytime = isDaytime(at = forecast.validAt)
                        )
                    }.toImmutableList()
            )
        }

private fun Forecast.toCurrentWeather(isDaytime: Boolean): CurrentWeather =
    CurrentWeather(
        temperature = airTemperature,
        feelsLikeTemperature = feelsLikeTemperature(),
        iconResourceId = WeatherForecastUtil.getWeatherSymbolIconResourceId(
            symbolCode = symbolCode,
            isDaytime = isDaytime
        ),
        descriptionResourceId = WeatherForecastUtil.getWsymb2ResourceId(symbolCode = symbolCode),
        windDirectionResourceId = WeatherForecastUtil.handleWindDirection(degree = windFromDirection),
        windSpeed = windSpeed,
        windArrowRotation = (windFromDirection.toFloat() + WIND_ARROW_ROTATION_OFFSET) % DEGREES_IN_CIRCLE,
        humidityPercent = relativeHumidity
    )

private fun Forecast.feelsLikeTemperature(): String? =
    WindChill.calculate(
        temperatureCelcius = airTemperature,
        windSpeedMeterPerSecond = windSpeed
    )

private fun Forecast.toForecastHour(
    timeZone: TimeZone,
    isDaytime: Boolean
): ForecastHour =
    ForecastHour(
        key = "$HOUR_KEY_PREFIX$validAt",
        timeText = validAt
            .toLocalDateTime(timeZone = timeZone)
            .time
            .format(format = hourFormat),
        temperature = airTemperature,
        feelsLikeTemperature = feelsLikeTemperature(),
        descriptionResourceId = WeatherForecastUtil.getWsymb2ResourceId(symbolCode = symbolCode),
        iconResourceId = WeatherForecastUtil.getWeatherSymbolIconResourceId(
            symbolCode = symbolCode,
            isDaytime = isDaytime
        )
    )