package fi.kroon.vadret.weatherforecast.model

import androidx.annotation.StringRes
import fi.kroon.vadret.icons.VadretIconGraphic

data class ForecastHour(
    val key: String,
    val timeText: String,
    val temperature: Double,
    val feelsLikeTemperature: String?,
    @StringRes
    val descriptionResourceId: Int,
    val icon: VadretIconGraphic
)