package fi.kroon.vadret.weatherforecast.model

data class WeatherForecastHeadlineModel(
    val headline: Int?,
    val windSpeed: Double?,
    val windDirection: Double?
) : IWeatherForecastModel