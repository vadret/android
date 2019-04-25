package fi.kroon.vadret.presentation.weatherforecastwidget.small.provider.model

data class WeatherForecastSmallModel(
    val temperature: Double?,
    val feelsLikeTemperature: String?,
    val thunderProbability: Int?,
    val humidityPercent: Int?,
    val windSpeed: Double?,
    val precipitationCode: Int?,
    val localityName: String?,
    val wsymb2Icon: Int?,
    val riskForThunder: Boolean = false
)