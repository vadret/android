package fi.kroon.vadret.data.weather.model

import com.squareup.moshi.Json
import java.io.Serializable

data class Geometry(
    @Json(name = "type")
    val type: String = "Point",
    @Json(name = "coordinates")
    val coordinates: List<List<Double>>

) : Serializable