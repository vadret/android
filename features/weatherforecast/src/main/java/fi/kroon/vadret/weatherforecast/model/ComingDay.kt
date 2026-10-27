package fi.kroon.vadret.weatherforecast.model

import androidx.annotation.StringRes
import fi.kroon.vadret.icons.VadretIconGraphic
import kotlinx.collections.immutable.ImmutableList

data class ComingDay(
    val key: String,
    val isToday: Boolean,
    val weekdayText: String,
    val dateText: String,
    val icon: VadretIconGraphic,
    @StringRes
    val descriptionResourceId: Int,
    val precipitationText: String?,
    val lowTemperatureText: String,
    val highTemperatureText: String,
    val temperatureRangeStart: Float,
    val temperatureRangeEnd: Float,
    val hours: ImmutableList<ComingHour>,
    val firstShownHourIndex: Int,
    val isExpanded: Boolean
)