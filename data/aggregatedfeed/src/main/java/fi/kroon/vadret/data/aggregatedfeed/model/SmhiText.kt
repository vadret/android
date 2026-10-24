package fi.kroon.vadret.data.aggregatedfeed.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SmhiText(

    @SerialName("sv")
    val sv: String,

    @SerialName("code")
    val code: String?
)