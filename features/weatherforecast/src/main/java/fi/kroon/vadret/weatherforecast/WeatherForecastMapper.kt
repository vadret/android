package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.data.location.model.Location
import fi.kroon.vadret.data.weatherforecast.model.TimeSerie
import fi.kroon.vadret.data.weatherforecast.model.WeatherData
import fi.kroon.vadret.util.common.SunriseSunset
import fi.kroon.vadret.util.common.SunsetUtil
import fi.kroon.vadret.util.common.WeatherForecastUtil
import fi.kroon.vadret.util.common.WindChill
import fi.kroon.vadret.weatherforecast.model.WeatherForecast
import fi.kroon.vadret.weatherforecast.model.WeatherForecastRow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
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

/**
 *  Groups the forecast by local date, the times from SMHI are in UTC.
 */
internal object WeatherForecastMapper {

    private const val WEEKDAY_PATTERN = "EEEE"

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

    operator fun invoke(
        timeSeries: List<TimeSerie>,
        location: Location,
        now: Instant,
        timeZone: TimeZone,
        locale: Locale
    ): WeatherForecast {
        val first: TimeSerie = timeSeries.firstOrNull() ?: return WeatherForecast(
            overview = emptyList(),
            days = emptyList()
        )
        val dateFormat: DateFormat = DateFormat.getDateInstance(DateFormat.LONG, locale)
        val timeFormat: DateFormat = DateFormat.getTimeInstance(DateFormat.SHORT, locale)
        val weekdayFormat = SimpleDateFormat(WEEKDAY_PATTERN, locale)
        val javaTimeZone: java.util.TimeZone = java.util.TimeZone.getTimeZone(timeZone.id)
        listOf(dateFormat, timeFormat, weekdayFormat).forEach { format: DateFormat ->
            format.timeZone = javaTimeZone
        }

        val overview: List<WeatherForecastRow> = listOf(
            toHeadline(data = first.data),
            toSummary(
                data = first.data,
                sunriseSunset = SunsetUtil.getSunriseSunset(
                    day = now,
                    latitude = location.latitude,
                    longitude = location.longitude
                ),
                timeFormat = timeFormat
            )
        )
        val days: List<WeatherForecastRow> = buildList {
            timeSeries
                .groupBy { timeSerie: TimeSerie -> toLocalDateTime(timeSerie = timeSerie, timeZone = timeZone).date }
                .forEach { (date: LocalDate, dayTimeSeries: List<TimeSerie>) ->
                    val startOfDay = Date(date.atStartOfDayIn(timeZone = timeZone).toEpochMilliseconds())
                    add(
                        WeatherForecastRow.Day(
                            key = "day-$date",
                            weekday = weekdayFormat.format(startOfDay).uppercase(locale = locale),
                            dateText = dateFormat.format(startOfDay)
                        )
                    )
                    dayTimeSeries.forEach { timeSerie: TimeSerie ->
                        add(toHour(timeSerie = timeSerie, timeZone = timeZone))
                    }
                }
        }
        return WeatherForecast(
            overview = overview,
            days = days
        )
    }

    private fun toLocalDateTime(timeSerie: TimeSerie, timeZone: TimeZone): LocalDateTime =
        Instant.parse(input = timeSerie.time).toLocalDateTime(timeZone = timeZone)

    private fun toHeadline(data: WeatherData): WeatherForecastRow.Headline =
        WeatherForecastRow.Headline(
            descriptionResourceId = WeatherForecastUtil.getWsymb2ResourceId(index = data.symbolCode),
            windSpeedClassResourceId = WeatherForecastUtil.getWindSpeedClassResourceId(windSpeed = data.windSpeed),
            windDirectionResourceId = WeatherForecastUtil.handleWindDirection(degree = data.windFromDirection),
            windArrowRotation = data.windFromDirection.toFloat() + WIND_ARROW_ROTATION_OFFSET
        )

    private fun toSummary(
        data: WeatherData,
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
            temperature = data.airTemperature,
            feelsLikeTemperature = WindChill.calculate(
                temperatureCelcius = data.airTemperature,
                windSpeedMeterPerSecond = data.windSpeed
            ),
            humidityPercent = data.relativeHumidity,
            windSpeed = data.windSpeed,
            precipitationResourceId = if (data.precipitationType > NO_PRECIPITATION) {
                WeatherForecastUtil.getPrecipitationResourceId(prSort = data.precipitationType)
            } else {
                null
            }
        )

    private fun toHour(timeSerie: TimeSerie, timeZone: TimeZone): WeatherForecastRow.Hour {
        val data: WeatherData = timeSerie.data
        return WeatherForecastRow.Hour(
            key = "hour-${timeSerie.time}",
            timeText = toLocalDateTime(timeSerie = timeSerie, timeZone = timeZone).time.format(format = hourFormat),
            temperature = data.airTemperature,
            feelsLikeTemperature = WindChill.calculate(
                temperatureCelcius = data.airTemperature,
                windSpeedMeterPerSecond = data.windSpeed
            ),
            descriptionResourceId = WeatherForecastUtil.getWsymb2ResourceId(index = data.symbolCode),
            iconResourceId = WeatherForecastUtil.getWsymb2IconResourceId(index = data.symbolCode),
            temperatureColorResourceId = WeatherForecastUtil.getTemperatureColorResourceId(temperature = data.airTemperature)
        )
    }
}