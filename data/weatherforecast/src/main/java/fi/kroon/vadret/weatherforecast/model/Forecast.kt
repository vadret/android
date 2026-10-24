package fi.kroon.vadret.weatherforecast.model

import kotlin.time.Instant

data class Forecast(
    val validAt: Instant,
    val airTemperature: Double,
    val windFromDirection: Double,
    val windSpeed: Double,
    val relativeHumidity: Int,
    val precipitationType: Int,
    val symbolCode: Int
)