package fi.kroon.vadret.data.aggregatedfeed.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KrisinformationLink(

    @Json(name = "text")
    val text: String?,

    @Json(name = "url")
    val url: String
)