package fi.kroon.vadret.weatherforecast.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class CurrentWeather(
    val temperature: Double,
    val feelsLikeTemperature: String?,
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