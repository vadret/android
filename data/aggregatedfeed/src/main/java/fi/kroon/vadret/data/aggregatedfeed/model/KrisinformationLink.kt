package fi.kroon.vadret.data.aggregatedfeed.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KrisinformationLink(

    @SerialName("text")
    val text: String?,

    @SerialName("url")
    val url: String
)