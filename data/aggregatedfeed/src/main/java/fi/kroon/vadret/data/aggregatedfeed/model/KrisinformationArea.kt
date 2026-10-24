package fi.kroon.vadret.data.aggregatedfeed.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KrisinformationArea(

    @Json(name = "type")
    val type: String?,

    @Json(name = "description")
    val description: String?,

    @Json(name = "coordinate")
    val coordinate: String?
)