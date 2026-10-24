package fi.kroon.vadret.data.aggregatedfeed.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SmhiDescription(

    @SerialName("title")
    val title: SmhiText,

    @SerialName("text")
    val text: SmhiText
)