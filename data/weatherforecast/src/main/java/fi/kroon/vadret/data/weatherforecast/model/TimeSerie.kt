package fi.kroon.vadret.data.weatherforecast.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimeSerie(
    @SerialName(value = "time")
    val time: String,
    @SerialName(value = "data")
    val data: WeatherData
)