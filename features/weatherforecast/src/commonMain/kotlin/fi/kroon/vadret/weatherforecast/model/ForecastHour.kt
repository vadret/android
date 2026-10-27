package fi.kroon.vadret.weatherforecast.model

import fi.kroon.vadret.icons.VadretIconGraphic
import org.jetbrains.compose.resources.StringResource

data class ForecastHour(
    val key: String,
    val timeText: String,
    val temperature: Double,
    val feelsLikeTemperature: String?,
    val descriptionResource: StringResource,
    val icon: VadretIconGraphic
)