package fi.kroon.vadret.weatherforecast.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.collections.immutable.ImmutableList

data class ComingDay(
    val key: String,
    val isToday: Boolean,
    val weekdayText: String,
    val dateText: String,
    @DrawableRes
    val iconResourceId: Int,
    @StringRes
    val descriptionResourceId: Int,
    val precipitationText: String?,
    val lowTemperatureText: String,
    val highTemperatureText: String,
    val temperatureRangeStart: Float,
    val temperatureRangeEnd: Float,
    val windSpeedText: String,
    val sunTimes: SunTimes,
    val hours: ImmutableList<ComingHour>,
    val isExpanded: Boolean
)