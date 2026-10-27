package fi.kroon.vadret.weatherforecast.ui

import fi.kroon.vadret.weatherforecast.WeatherForecastState
import fi.kroon.vadret.weatherforecast.extension.toWeatherForecast
import fi.kroon.vadret.weatherforecast.extension.withDayToggled
import fi.kroon.vadret.weatherforecast.model.Analysis
import fi.kroon.vadret.weatherforecast.model.ComingDay
import fi.kroon.vadret.weatherforecast.model.ComingHour
import fi.kroon.vadret.weatherforecast.model.CurrentWeather
import fi.kroon.vadret.weatherforecast.model.Forecast
import fi.kroon.vadret.weatherforecast.model.PlaceForecast
import fi.kroon.vadret.weatherforecast.model.WeatherForecast
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.TimeZone
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

private const val PREVIEW_LANGUAGE_TAG = "en"
private const val PREVIEW_LOCALITY = "Stockholm"
private const val PREVIEW_WARNING_COUNT = 3
private const val PREVIEW_LATITUDE = 59.3293
private const val PREVIEW_LONGITUDE = 18.0686
private const val FORECAST_HOURS = 168
private const val ANALYSIS_HOURS = 24
private const val FIRST_FORECAST_HOUR = 0
private const val INTERVAL_HOURS = 1
private const val FULL_TURN = 2 * PI
private const val HOURS_PER_DAY = 24.0
private const val HOURS_UNTIL_WARMEST = 5
private const val MEAN_TEMPERATURE = 7.0
private const val TEMPERATURE_SWING = 3.5
private const val COOLING_PER_HOUR = 0.03
private const val MEAN_WIND_SPEED = 5.0
private const val WIND_SWING = 3.0
private const val WIND_PERIOD_HOURS = 30.0
private const val FIRST_WIND_DIRECTION = 200.0
private const val WIND_TURN_PER_HOUR = 2.0
private const val DEGREES_IN_CIRCLE = 360.0
private const val RELATIVE_HUMIDITY = 78
private const val SYMBOL_PERIOD_HOURS = 6
private const val FIRST_PRECIPITATION_SYMBOL_CODE = 8
private const val RAIN_PRECIPITATION_TYPE = 3
private const val NO_PRECIPITATION_TYPE = 0
private const val RAIN_AMOUNT = 0.6
private const val NO_RAIN_AMOUNT = 0.0

private val previewNow: Instant = Instant.parse(input = "2026-10-26T08:00:00Z")
private val previewTimeZone: TimeZone = TimeZone.of(zoneId = "Europe/Stockholm")
private val previewSymbolCodes: List<Int> = listOf(2, 3, 6, 18, 5, 1, 3, 4, 19, 6, 2, 1)

internal fun weatherForecastPreviewState(): WeatherForecastState {
    val weatherForecast: WeatherForecast = previewWeatherForecast()
    val todayKey: String = weatherForecast.comingDays.first().key
    return WeatherForecastState(
        forecast = weatherForecast.copy(comingDays = weatherForecast.comingDays.withDayToggled(dayKey = todayKey)),
        localityName = PREVIEW_LOCALITY,
        warningCount = PREVIEW_WARNING_COUNT,
        isLoading = false
    )
}

internal fun currentWeatherPreview(): CurrentWeather = checkNotNull(value = previewWeatherForecast().current)

internal fun comingDaysPreview(): ImmutableList<ComingDay> = weatherForecastPreviewState().forecast.comingDays

internal fun comingHoursPreview(): ImmutableList<ComingHour> = comingDaysPreview().first().hours

internal fun nowHourIndexPreview(): Int = comingHoursPreview().indexOfFirst { comingHour: ComingHour -> comingHour.isNow }

private fun previewWeatherForecast(): WeatherForecast =
    PlaceForecast(
        placeName = PREVIEW_LOCALITY,
        latitude = PREVIEW_LATITUDE,
        longitude = PREVIEW_LONGITUDE,
        forecasts = previewHours(hourOffsets = FIRST_FORECAST_HOUR until FORECAST_HOURS),
        analyses = previewAnalyses()
    ).toWeatherForecast(
        now = previewNow,
        timeZone = previewTimeZone,
        languageTag = PREVIEW_LANGUAGE_TAG
    )

private fun previewAnalyses(): List<Analysis> =
    previewHours(hourOffsets = -ANALYSIS_HOURS until FIRST_FORECAST_HOUR)
        .map { forecast: Forecast -> forecast.toPreviewAnalysis() }

private fun Forecast.toPreviewAnalysis(): Analysis =
    Analysis(
        analysedAt = validAt,
        airTemperature = airTemperature,
        windFromDirection = windFromDirection,
        windSpeed = windSpeed,
        relativeHumidity = relativeHumidity,
        precipitationType = precipitationType,
        symbolCode = symbolCode,
        precipitationAmountLastHour = precipitationAmount
    )

private fun previewHours(hourOffsets: IntRange): List<Forecast> =
    hourOffsets.map { hourOffset: Int ->
        val validAt: Instant = previewNow + hourOffset.hours
        val symbolCode: Int = previewSymbolCodes[hourOffset.floorDiv(other = SYMBOL_PERIOD_HOURS).mod(other = previewSymbolCodes.size)]
        val isRaining: Boolean = symbolCode >= FIRST_PRECIPITATION_SYMBOL_CODE
        Forecast(
            validAt = validAt,
            intervalStartedAt = validAt - INTERVAL_HOURS.hours,
            airTemperature = round(
                x = MEAN_TEMPERATURE +
                    TEMPERATURE_SWING * cos(x = FULL_TURN * (hourOffset - HOURS_UNTIL_WARMEST) / HOURS_PER_DAY) -
                    COOLING_PER_HOUR * hourOffset
            ),
            windFromDirection = (FIRST_WIND_DIRECTION + WIND_TURN_PER_HOUR * hourOffset) % DEGREES_IN_CIRCLE,
            windSpeed = MEAN_WIND_SPEED + WIND_SWING * sin(x = FULL_TURN * hourOffset / WIND_PERIOD_HOURS),
            relativeHumidity = RELATIVE_HUMIDITY,
            precipitationType = if (isRaining) {
                RAIN_PRECIPITATION_TYPE
            } else {
                NO_PRECIPITATION_TYPE
            },
            symbolCode = symbolCode,
            precipitationAmount = if (isRaining) {
                RAIN_AMOUNT
            } else {
                NO_RAIN_AMOUNT
            }
        )
    }