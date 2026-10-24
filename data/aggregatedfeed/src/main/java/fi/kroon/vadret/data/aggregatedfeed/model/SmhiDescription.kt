package fi.kroon.vadret.data.aggregatedfeed.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SmhiDescription(

    @Json(name = "title")
    val title: SmhiText,

    @Json(name = "text")
    val text: SmhiText
)