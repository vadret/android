package fi.kroon.vadret.weatherforecast.model

data class PlaceForecast(
    val placeName: String?,
    val latitude: Double,
    val longitude: Double,
    val forecasts: List<Forecast>
)