package fi.kroon.vadret.presentation.weatherforecast.model

import org.threeten.bp.OffsetDateTime

data class WeatherForecastSplashItemModel(
    val sunriseDateTime: OffsetDateTime? = null,
    val sunsetDateTime: OffsetDateTime? = null,
    val humidityPercent: Int,
    val temperature: Double,
    val windSpeed: Double,
    val windDirection: Double?,
    val feelsLikeTemperature: String? = null,
    val weatherIcon: Int,
    val weatherDescription: Int,
    val precipitationCode: Int? = null,
    val thunderRisk: Int
) : BaseWeatherForecastModel