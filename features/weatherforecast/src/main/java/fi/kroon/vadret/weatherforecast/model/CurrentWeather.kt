package fi.kroon.vadret.weatherforecast.model

import androidx.annotation.StringRes
import fi.kroon.vadret.icons.VadretIconGraphic

data class CurrentWeather(
    val temperatureText: String,
    val feelsLikeTemperatureText: String,
    val icon: VadretIconGraphic,
    @StringRes
    val descriptionResourceId: Int,
    @StringRes
    val windDirectionResourceId: Int,
    val windSpeed: Double,
    val windArrowRotation: Float,
    val humidityPercent: Int
)