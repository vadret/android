package fi.kroon.vadret.data.aggregatedfeed.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 *  [id] matches the district ids from SMHI's area metadata.
 */
@JsonClass(generateAdapter = true)
data class SmhiAffectedArea(

    @Json(name = "id")
    val id: Int,

    @Json(name = "sv")
    val sv: String
)