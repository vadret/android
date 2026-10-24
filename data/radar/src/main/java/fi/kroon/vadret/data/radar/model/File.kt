package fi.kroon.vadret.data.radar.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class File(

    @SerialName("key")
    val key: String,

    @SerialName("valid")
    val valid: String,

    @SerialName("formats")
    val formats: List<Format>
) : java.io.Serializable