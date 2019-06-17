package fi.kroon.vadret.data.district.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class District(
    @Json(name = "id")
    val id: String,

    @Json(name = "sort_order")
    val sortOrder: String,

    @Json(name = "category")
    val category: String,

    @Json(name = "name")
    val name: String,

    @Json(name = "geometry")
    val geometry: Geometry

)