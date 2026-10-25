package fi.kroon.vadret.weatherforecast.model

internal data class PlaceForecast(
    val placeName: String?,
    val latitude: Double,
    val longitude: Double,
    val forecasts: List<Forecast>
)