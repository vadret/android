package fi.kroon.vadret.data.district.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Geometry(

    @Json(name = "point")
    val point: String,

    @Json(name = "polygon")
    val polygon: String
)