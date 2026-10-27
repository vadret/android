package fi.kroon.vadret.aggregatedfeed.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SmhiWarningAreaDto(
    @SerialName(value = "id")
    val id: Int,
    @SerialName(value = "published")
    val published: String,
    @SerialName(value = "areaName")
    val areaName: SmhiTextDto,
    @SerialName(value = "warningLevel")
    val warningLevel: SmhiTextDto,
    @SerialName(value = "eventDescription")
    val eventDescription: SmhiTextDto,
    @SerialName(value = "affectedAreas")
    val affectedAreas: List<SmhiAffectedAreaDto>? = null,
    @SerialName(value = "descriptions")
    val descriptions: List<SmhiDescriptionDto>? = null
)