package fi.kroon.vadret.data.radar.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Radar(

    @SerialName("key")
    val key: String,

    @SerialName("updated")
    val updated: String,

    @SerialName("timeZone")
    val timeZone: String,

    @SerialName("downloads")
    val downloads: List<Download>,

    @SerialName("files")
    val files: List<File>
) : java.io.Serializable