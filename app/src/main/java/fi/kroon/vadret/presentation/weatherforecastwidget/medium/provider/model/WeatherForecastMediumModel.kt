package fi.kroon.vadret.presentation.weatherforecastwidget.medium.provider.model

data class WeatherForecastMediumModel(
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