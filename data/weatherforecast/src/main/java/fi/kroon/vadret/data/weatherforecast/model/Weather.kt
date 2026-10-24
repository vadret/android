package fi.kroon.vadret.data.weatherforecast.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Weather(

    @SerialName(value = "approvedTime")
    val approvedTime: String,

    @SerialName(value = "referenceTime")
    val referenceTime: String,

    @SerialName(value = "geometry")
    val geometry: Geometry,

    @SerialName(value = "timeSeries")
    val timeSeries: List<TimeSerie> = emptyList()

)