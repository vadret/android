package fi.kroon.vadret.data.aggregatedfeed.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SmhiWarningArea(

    @Json(name = "id")
    val id: Int,

    @Json(name = "published")
    val published: String,

    @Json(name = "pushNotice")
    val pushNotice: Boolean,

    @Json(name = "areaName")
    val areaName: SmhiText,

    @Json(name = "warningLevel")
    val warningLevel: SmhiText,

    @Json(name = "eventDescription")
    val eventDescription: SmhiText,

    @Json(name = "affectedAreas")
    val affectedAreas: List<SmhiAffectedArea>?,

    @Json(name = "descriptions")
    val descriptions: List<SmhiDescription>?
)