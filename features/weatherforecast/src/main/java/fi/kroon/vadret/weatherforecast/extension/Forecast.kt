package fi.kroon.vadret.weatherforecast.extension

import fi.kroon.vadret.weatherforecast.model.ComingDay
import fi.kroon.vadret.weatherforecast.model.ComingHour
import fi.kroon.vadret.weatherforecast.model.Forecast
import fi.kroon.vadret.weatherforecast.model.SunTimes
import fi.kroon.vadret.weatherforecast.util.SunriseSunset
import fi.kroon.vadret.weatherforecast.util.SunsetUtil
import fi.kroon.vadret.weatherforecast.util.WeatherForecastUtil
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Instant
import java.util.TimeZone as JavaTimeZone

private const val COMING_DAY_KEY_PREFIX = "coming-day-"
private const val COMING_HOUR_KEY_PREFIX = "coming-hour-"
private const val SHORT_WEEKDAY_PATTERN = "EEE"
private const val SHORT_DATE_PATTERN = "d MMM"
private const val DAYTIME_START_HOUR = 6
private const val DAYTIME_END_HOUR = 18
private const val MIDDAY_HOUR = 12
private const val MIDDAY_MINUTE = 0
private const val FIRST_HOUR_INDEX = 0
private const val MIDPOINT_DIVISOR = 2
private const val ONE_DECIMAL = "%.1f"
private const val SHOWN_PRECIPITATION = 0.1
private const val HOUR_DIGITS = 2
private const val HOUR_PADDING = '0'
private const val RANGE_START = 0f
private const val RANGE_END = 1f
private const val NO_SPAN = 0.0

private class DaySummary(
    val date: LocalDate,
    val forecasts: List<Forecast>,
    val lowTemperature: Double,
    val highTemperature: Double,
    val precipitationAmount: Double?
)

private class TemperatureScale(
    val low: Double,
    val high: Double
)

private class DayFormats(
    val weekdayFormat: DateFormat,
    val dateFormat: DateFormat,
    val timeFormat: DateFormat
)

internal fun List<Forecast>.toComingDays(
    now: Instant,
    timeZone: TimeZone,
    locale: Locale,
    latitude: Double,
    longitude: Double,
    timeFormat: DateFormat
): List<ComingDay> {
    val today: LocalDate = now.toLocalDateTime(timeZone = timeZone).date
    val summaries: List<DaySummary> = toDaySummaries(timeZone = timeZone)
        .filter { daySummary: DaySummary ->
            daySummary.date == today || daySummary.hasDaytime(timeZone = timeZone)
        }
    if (summaries.isEmpty()) {
        return emptyList()
    }
    val temperatureScale = TemperatureScale(
        low = summaries.minOf { daySummary: DaySummary -> daySummary.lowTemperature },
        high = summaries.maxOf { daySummary: DaySummary -> daySummary.highTemperature }
    )
    val javaTimeZone: JavaTimeZone = JavaTimeZone.getTimeZone(timeZone.id)
    val dayFormats = DayFormats(
        weekdayFormat = SimpleDateFormat(SHORT_WEEKDAY_PATTERN, locale)
            .withTimeZone(javaTimeZone = javaTimeZone),
        dateFormat = SimpleDateFormat(SHORT_DATE_PATTERN, locale)
            .withTimeZone(javaTimeZone = javaTimeZone),
        timeFormat = timeFormat
    )
    return summaries.map { daySummary: DaySummary ->
        daySummary.toComingDay(
            isToday = daySummary.date == today,
            timeZone = timeZone,
            locale = locale,
            latitude = latitude,
            longitude = longitude,
            temperatureScale = temperatureScale,
            dayFormats = dayFormats
        )
    }
}

private fun List<Forecast>.toDaySummaries(timeZone: TimeZone): List<DaySummary> {
    val precipitationByDate: Map<LocalDate, Double?> = groupBy { forecast: Forecast ->
        forecast.intervalMidpoint().toLocalDateTime(timeZone = timeZone).date
    }.mapValues { (_, intervalForecasts: List<Forecast>) ->
        intervalForecasts.totalPrecipitationAmount()
    }
    return groupBy { forecast: Forecast -> forecast.validAt.toLocalDateTime(timeZone = timeZone).date }
        .map { (date: LocalDate, dayForecasts: List<Forecast>) ->
            DaySummary(
                date = date,
                forecasts = dayForecasts,
                lowTemperature = dayForecasts.minOf { forecast: Forecast -> forecast.airTemperature },
                highTemperature = dayForecasts.maxOf { forecast: Forecast -> forecast.airTemperature },
                precipitationAmount = precipitationByDate[date]
            )
        }
}

private fun Forecast.intervalMidpoint(): Instant {
    val intervalLength: Duration = validAt - intervalStartedAt
    return intervalStartedAt + intervalLength / MIDPOINT_DIVISOR
}

private fun List<Forecast>.totalPrecipitationAmount(): Double? {
    val knownAmounts: List<Double> = mapNotNull { forecast: Forecast -> forecast.precipitationAmount }
    return if (knownAmounts.size == size) {
        knownAmounts.sum()
    } else {
        null
    }
}

private fun DaySummary.hasDaytime(timeZone: TimeZone): Boolean =
    forecasts.any { forecast: Forecast ->
        forecast.isDaytimeHour(timeZone = timeZone)
    }

private fun Forecast.isDaytimeHour(timeZone: TimeZone): Boolean =
    validAt
        .toLocalDateTime(timeZone = timeZone)
        .hour in DAYTIME_START_HOUR..DAYTIME_END_HOUR

private fun DaySummary.toComingDay(
    isToday: Boolean,
    timeZone: TimeZone,
    locale: Locale,
    latitude: Double,
    longitude: Double,
    temperatureScale: TemperatureScale,
    dayFormats: DayFormats
): ComingDay {
    val midday: Instant = date
        .atTime(
            hour = MIDDAY_HOUR,
            minute = MIDDAY_MINUTE
        ).toInstant(timeZone = timeZone)
    val middayDate = Date(midday.toEpochMilliseconds())
    val symbolCode: Int = representativeSymbolCode(timeZone = timeZone)
    return ComingDay(
        key = "$COMING_DAY_KEY_PREFIX$date",
        isToday = isToday,
        weekdayText = dayFormats.weekdayFormat
            .format(middayDate)
            .replaceFirstChar { character: Char -> character.titlecase(locale = locale) },
        dateText = dayFormats.dateFormat.format(middayDate),
        iconResourceId = WeatherForecastUtil.getWeatherSymbolIconResourceId(
            symbolCode = symbolCode,
            isDaytime = true
        ),
        descriptionResourceId = WeatherForecastUtil.getWsymb2ResourceId(symbolCode = symbolCode),
        precipitationText = precipitationAmount.toPrecipitationText(),
        lowTemperatureText = lowTemperature.roundToInt().toString(),
        highTemperatureText = highTemperature.roundToInt().toString(),
        temperatureRangeStart = temperatureScale.fractionOf(
            temperature = lowTemperature,
            fallback = RANGE_START
        ),
        temperatureRangeEnd = temperatureScale.fractionOf(
            temperature = highTemperature,
            fallback = RANGE_END
        ),
        windSpeedText = ONE_DECIMAL.format(
            Locale.ROOT,
            forecasts.maxOf { forecast: Forecast -> forecast.windSpeed }
        ),
        sunTimes = SunsetUtil
            .getSunriseSunset(
                day = midday,
                latitude = latitude,
                longitude = longitude
            ).toSunTimes(
                isSunUpAtMidday = SunsetUtil.isSunUp(
                    at = midday,
                    latitude = latitude,
                    longitude = longitude
                ),
                timeFormat = dayFormats.timeFormat
            ),
        hours = forecasts.mapIndexed { index: Int, forecast: Forecast ->
            forecast.toComingHour(
                isNow = isToday && index == FIRST_HOUR_INDEX,
                timeZone = timeZone,
                isDaytime = SunsetUtil.isSunUp(
                    at = forecast.validAt,
                    latitude = latitude,
                    longitude = longitude
                )
            )
        }.toImmutableList(),
        isExpanded = false
    )
}

private fun DaySummary.representativeSymbolCode(timeZone: TimeZone): Int {
    val daytimeForecasts: List<Forecast> = forecasts.filter { forecast: Forecast ->
        forecast.isDaytimeHour(timeZone = timeZone)
    }
    return daytimeForecasts
        .ifEmpty { forecasts }
        .groupingBy { forecast: Forecast -> forecast.symbolCode }
        .eachCount()
        .entries
        .maxWith(
            comparator = compareBy<Map.Entry<Int, Int>> { entry: Map.Entry<Int, Int> -> entry.value }
                .thenBy { entry: Map.Entry<Int, Int> -> entry.key }
        ).key
}

private fun TemperatureScale.fractionOf(
    temperature: Double,
    fallback: Float
): Float {
    val span: Double = high - low
    return if (span > NO_SPAN) {
        ((temperature - low) / span).toFloat()
    } else {
        fallback
    }
}

private fun SunriseSunset?.toSunTimes(
    isSunUpAtMidday: Boolean,
    timeFormat: DateFormat
): SunTimes {
    if (this == null) {
        return if (isSunUpAtMidday) {
            SunTimes.MidnightSun
        } else {
            SunTimes.PolarNight
        }
    }
    return SunTimes.RiseAndSet(
        sunriseText = sunrise.toTimeText(timeFormat = timeFormat),
        sunsetText = sunset.toTimeText(timeFormat = timeFormat)
    )
}

private fun Forecast.toComingHour(
    isNow: Boolean,
    timeZone: TimeZone,
    isDaytime: Boolean
): ComingHour =
    ComingHour(
        key = "$COMING_HOUR_KEY_PREFIX$validAt",
        isNow = isNow,
        timeText = validAt
            .toLocalDateTime(timeZone = timeZone)
            .hour
            .toString()
            .padStart(
                length = HOUR_DIGITS,
                padChar = HOUR_PADDING
            ),
        iconResourceId = WeatherForecastUtil.getWeatherSymbolIconResourceId(
            symbolCode = symbolCode,
            isDaytime = isDaytime
        ),
        descriptionResourceId = WeatherForecastUtil.getWsymb2ResourceId(symbolCode = symbolCode),
        temperatureText = airTemperature.roundToInt().toString(),
        precipitationText = precipitationAmount.toPrecipitationText()
    )

private fun Double?.toPrecipitationText(): String? =
    if (this != null && this >= SHOWN_PRECIPITATION) {
        ONE_DECIMAL.format(Locale.ROOT, this)
    } else {
        null
    }

private fun Instant.toTimeText(timeFormat: DateFormat): String =
    timeFormat.format(
        Date(toEpochMilliseconds())
    )

private fun DateFormat.withTimeZone(javaTimeZone: JavaTimeZone): DateFormat {
    timeZone = javaTimeZone
    return this
}