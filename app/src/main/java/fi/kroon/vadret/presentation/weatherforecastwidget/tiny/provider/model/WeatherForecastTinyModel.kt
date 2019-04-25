package fi.kroon.vadret.presentation.weatherforecastwidget.tiny.provider.model

import fi.kroon.vadret.presentation.weatherforecast.model.IWeatherForecastModel

data class WeatherForecastTinyModel(
    val temperature: Double?,
    val localityName: String?
) : IWeatherForecastModel