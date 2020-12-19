package fi.kroon.vadret.data.weatherforecast.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Geometry(
    @SerialName(value = "type")
    val type: String = "Point",
    @SerialName(value = "coordinates")
    val coordinates: List<List<Double>>
)