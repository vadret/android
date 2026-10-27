package fi.kroon.vadret.weatherforecast.model

import fi.kroon.vadret.icons.VadretIconGraphic
import org.jetbrains.compose.resources.StringResource

data class CurrentWeather(
    val temperatureText: String,
    val feelsLikeTemperatureText: String,
    val icon: VadretIconGraphic,
    val descriptionResource: StringResource,
    val windDirectionResource: StringResource,
    val windSpeed: Double,
    val windArrowRotation: Float,
    val humidityPercent: Int
)