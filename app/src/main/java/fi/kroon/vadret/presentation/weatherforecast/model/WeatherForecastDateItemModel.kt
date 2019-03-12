package fi.kroon.vadret.presentation.weatherforecast.model

import org.threeten.bp.LocalDate

data class WeatherForecastDateItemModel(
    val date: LocalDate
) : BaseWeatherForecastModel