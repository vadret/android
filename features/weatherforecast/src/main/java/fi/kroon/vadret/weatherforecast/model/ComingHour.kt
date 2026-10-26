package fi.kroon.vadret.weatherforecast.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class ComingHour(
    val key: String,
    val isNow: Boolean,
    val timeText: String,
    @DrawableRes
    val iconResourceId: Int,
    @StringRes
    val descriptionResourceId: Int,
    val temperatureText: String,
    val temperatureLevel: Float,
    val precipitationText: String?,
    val windSpeedText: String,
    val windLevel: Float,
    val windArrowRotation: Float,
    @StringRes
    val windDirectionResourceId: Int
)