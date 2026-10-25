package fi.kroon.vadret.weatherforecast.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class ForecastHour(
    val key: String,
    val timeText: String,
    val temperature: Double,
    val feelsLikeTemperature: String?,
    @StringRes
    val descriptionResourceId: Int,
    @DrawableRes
    val iconResourceId: Int
)