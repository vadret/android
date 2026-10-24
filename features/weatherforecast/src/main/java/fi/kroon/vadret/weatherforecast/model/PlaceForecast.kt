package fi.kroon.vadret.weatherforecast.model

/**
 *  The forecast for one place. [placeName] is null when
 *  the device location could not be named.
 */
data class PlaceForecast(
    val placeName: String?,
    val latitude: Double,
    val longitude: Double,
    val forecasts: List<Forecast>
)