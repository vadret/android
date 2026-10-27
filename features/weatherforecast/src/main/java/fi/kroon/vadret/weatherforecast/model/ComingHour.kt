package fi.kroon.vadret.weatherforecast.model

import androidx.annotation.StringRes
import fi.kroon.vadret.icons.VadretIconGraphic

data class ComingHour(
    val key: String,
    val isNow: Boolean,
    val timeText: String,
    val icon: VadretIconGraphic,
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