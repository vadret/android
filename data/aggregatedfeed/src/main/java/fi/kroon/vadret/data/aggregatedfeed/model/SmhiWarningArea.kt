package fi.kroon.vadret.data.aggregatedfeed.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SmhiWarningArea(

    @SerialName("id")
    val id: Int,

    @SerialName("published")
    val published: String,

    @SerialName("pushNotice")
    val pushNotice: Boolean,

    @SerialName("areaName")
    val areaName: SmhiText,

    @SerialName("warningLevel")
    val warningLevel: SmhiText,

    @SerialName("eventDescription")
    val eventDescription: SmhiText,

    @SerialName("affectedAreas")
    val affectedAreas: List<SmhiAffectedArea>?,

    @SerialName("descriptions")
    val descriptions: List<SmhiDescription>?
)