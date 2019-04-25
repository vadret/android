package fi.kroon.vadret.presentation.weatherforecastwidget.medium.setup.model

data class WeatherForecastMediumConfigurationModel(
    val appWidgetId: Int,
    val theme: String,
    val updateInterval: String,
    val forecastFormat: Int
)