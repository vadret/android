package fi.kroon.vadret.weatherforecast.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class WeatherForecast(
    val current: CurrentWeather? = null,
    val days: ImmutableList<ForecastDay> = persistentListOf()
)