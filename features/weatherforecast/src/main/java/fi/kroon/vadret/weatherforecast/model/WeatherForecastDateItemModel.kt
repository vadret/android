package fi.kroon.vadret.weatherforecast.model

import org.threeten.bp.LocalDate

data class WeatherForecastDateItemModel(
    val date: LocalDate
) : IWeatherForecastModel