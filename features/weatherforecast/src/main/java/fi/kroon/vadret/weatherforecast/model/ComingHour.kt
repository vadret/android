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
    val precipitationText: String?
)