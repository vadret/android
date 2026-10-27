package fi.kroon.vadret.weatherforecast.extension

import fi.kroon.vadret.weatherforecast.model.Analysis
import fi.kroon.vadret.weatherforecast.remote.AnalysisTimeSeriesDto
import kotlin.time.Instant

private const val MISSING_VALUE = 9999.0
private const val NO_PRECIPITATION = 0.0

internal fun AnalysisTimeSeriesDto.toAnalysis(): Analysis =
    Analysis(
        analysedAt = Instant.parse(input = time),
        airTemperature = data.airTemperature,
        windFromDirection = data.windFromDirection,
        windSpeed = data.windSpeed,
        relativeHumidity = data.relativeHumidity,
        precipitationType = data.precipitationType,
        symbolCode = data.symbolCode,
        precipitationAmountLastHour = data.precipitationAmountLastHour.toPrecipitationAmount()
    )

private fun Double?.toPrecipitationAmount(): Double? =
    if (this == null || this >= MISSING_VALUE || this < NO_PRECIPITATION) {
        null
    } else {
        this
    }