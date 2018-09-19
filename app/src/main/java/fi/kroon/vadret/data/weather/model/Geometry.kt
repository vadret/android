package fi.kroon.vadret.data.weather.model

import com.squareup.moshi.Json

data class Geometry(
    @Json(name = "type")
    val type: String = "Point",
    @Json(name = "coordinates")
    val coordinates: List<List<Double>>

)