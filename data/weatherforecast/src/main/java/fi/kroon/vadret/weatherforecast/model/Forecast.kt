package fi.kroon.vadret.weatherforecast.model

import kotlin.time.Instant

/**
 *  The weather SMHI expects at [validAt].
 *
 *  @param precipitationType ECMWF parameter 260015, 0 when nothing falls.
 *  @param symbolCode SMHI's weather symbol, 1 to 27.
 */
data class Forecast(
    val validAt: Instant,
    val airTemperature: Double,
    val windFromDirection: Double,
    val windSpeed: Double,
    val relativeHumidity: Int,
    val precipitationType: Int,
    val symbolCode: Int
)