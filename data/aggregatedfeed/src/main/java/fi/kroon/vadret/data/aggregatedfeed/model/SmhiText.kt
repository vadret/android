package fi.kroon.vadret.data.aggregatedfeed.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SmhiText(

    @Json(name = "sv")
    val sv: String,

    @Json(name = "code")
    val code: String?
)