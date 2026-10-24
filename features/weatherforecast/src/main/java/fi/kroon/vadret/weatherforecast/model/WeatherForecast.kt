package fi.kroon.vadret.weatherforecast.model

import kotlinx.collections.immutable.ImmutableList

data class WeatherForecast(
    val overview: ImmutableList<WeatherForecastRow>,
    val days: ImmutableList<WeatherForecastRow>
)