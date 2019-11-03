package fi.kroon.vadret.data.weatherforecast.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Geometry(
    @Json(name = "type")
    val type: String = "Point",
    @Json(name = "coordinates")
    val coordinates: List<List<Double>>

) : Serializable