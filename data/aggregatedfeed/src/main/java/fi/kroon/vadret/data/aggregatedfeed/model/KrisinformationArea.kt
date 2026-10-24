package fi.kroon.vadret.data.aggregatedfeed.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KrisinformationArea(

    @SerialName("type")
    val type: String?,

    @SerialName("description")
    val description: String?,

    @SerialName("coordinate")
    val coordinate: String?
)