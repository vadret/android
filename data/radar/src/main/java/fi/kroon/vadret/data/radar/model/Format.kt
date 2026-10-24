package fi.kroon.vadret.data.radar.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Format(

    @SerialName("key")
    val key: String,

    @SerialName("updated")
    val updated: String,

    @SerialName("link")
    val link: String
) : java.io.Serializable