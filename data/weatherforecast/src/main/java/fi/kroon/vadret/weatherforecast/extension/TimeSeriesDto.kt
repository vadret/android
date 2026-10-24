package fi.kroon.vadret.weatherforecast.extension

import fi.kroon.vadret.weatherforecast.model.Forecast
import fi.kroon.vadret.weatherforecast.remote.TimeSeriesDto
import kotlin.time.Instant

internal fun TimeSeriesDto.toForecast(): Forecast =
    Forecast(
        validAt = Instant.parse(input = time),
        airTemperature = data.airTemperature,
        windFromDirection = data.windFromDirection,
        windSpeed = data.windSpeed,
        relativeHumidity = data.relativeHumidity,
        precipitationType = data.precipitationType,
        symbolCode = data.symbolCode
    )