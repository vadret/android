package fi.kroon.vadret.data.weatherforecast.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Parameter(

    @SerialName(value = "name")
    val name: String,

    @SerialName(value = "levelType")
    val levelType: String,

    @SerialName(value = "level")
    val level: Int,

    @SerialName(value = "unit")
    val unit: String,

    @SerialName(value = "values")
    val values: List<Double> = emptyList()
)