package fi.kroon.vadret.data.weather.model

import com.squareup.moshi.Json

data class Parameter(

    @Json(name = "name")
    val name: String,

    @Json(name = "levelType")
    val levelType: String,

    @Json(name = "level")
    val level: String,

    @Json(name = "unit")
    val unit: String,

    @Json(name = "values")
    val values: List<Double>
)