package fi.kroon.vadret.data.weatherforecast.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimeSerie(
    @SerialName(value = "validTime")
    val validTime: String,
    @SerialName(value = "parameters")
    val parameters: List<Parameter> = emptyList()
)