package fi.kroon.vadret.presentation.weatherforecastwidget.medium.service.model

data class WeatherForecastMediumServiceModel(
    val dateTime: String,
    val temperature: Double,
    val weatherIconResource: Int,
    val windSpeed: Double
)