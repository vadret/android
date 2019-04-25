package fi.kroon.vadret.presentation.weatherforecast.model

data class WeatherForecastHeadlineModel(
    val headline: Int?,
    val windSpeed: Double?,
    val windDirection: Double?
) : IWeatherForecastModel