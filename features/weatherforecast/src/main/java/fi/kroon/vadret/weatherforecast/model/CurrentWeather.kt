package fi.kroon.vadret.weatherforecast.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class CurrentWeather(
    val temperatureText: String,
    val feelsLikeTemperatureText: String,
    @DrawableRes
    val iconResourceId: Int,
    @StringRes
    val descriptionResourceId: Int,
    @StringRes
    val windDirectionResourceId: Int,
    val windSpeed: Double,
    val windArrowRotation: Float,
    val humidityPercent: Int
)