package fi.kroon.vadret.weatherforecast.extension

import fi.kroon.vadret.weatherforecast.model.Analysis
import fi.kroon.vadret.weatherforecast.model.Forecast
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

private val analysisInterval: Duration = 1.hours

internal fun Analysis.toForecast(): Forecast =
    Forecast(
        validAt = analysedAt,
        intervalStartedAt = analysedAt - analysisInterval,
        airTemperature = airTemperature,
        windFromDirection = windFromDirection,
        windSpeed = windSpeed,
        relativeHumidity = relativeHumidity,
        precipitationType = precipitationType,
        symbolCode = symbolCode,
        precipitationAmount = precipitationAmountLastHour
    )