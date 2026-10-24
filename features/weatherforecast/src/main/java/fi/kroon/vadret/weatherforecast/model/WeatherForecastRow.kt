package fi.kroon.vadret.weatherforecast.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed interface WeatherForecastRow {

    val key: String

    data class Headline(
        @StringRes
        val descriptionResourceId: Int,
        @StringRes
        val windSpeedClassResourceId: Int,
        @StringRes
        val windDirectionResourceId: Int,
        val windArrowRotation: Float
    ) : WeatherForecastRow {
        override val key: String = "headline"
    }

    data class Summary(
        val sunriseText: String?,
        val sunsetText: String?,
        val temperature: Double,
        val feelsLikeTemperature: String?,
        val humidityPercent: Int,
        val windSpeed: Double,
        @StringRes
        val precipitationResourceId: Int?
    ) : WeatherForecastRow {
        override val key: String = "summary"
    }

    data class Day(
        override val key: String,
        val weekday: String,
        val dateText: String
    ) : WeatherForecastRow

    data class Hour(
        override val key: String,
        val timeText: String,
        val temperature: Double,
        val feelsLikeTemperature: String?,
        @StringRes
        val descriptionResourceId: Int,
        @DrawableRes
        val iconResourceId: Int,
        @ColorRes
        val temperatureColorResourceId: Int
    ) : WeatherForecastRow
}