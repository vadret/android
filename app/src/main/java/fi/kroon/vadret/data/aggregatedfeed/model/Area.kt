package fi.kroon.vadret.data.aggregatedfeed.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import fi.kroon.vadret.util.extension.empty
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Area(

    @Json(name = "Type")
    val type: String,

    @Json(name = "Description")
    val description: String,

    @Json(name = "Coordinate")
    val coordinate: String? = String.empty()

) : Serializable