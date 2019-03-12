package fi.kroon.vadret.data.weatherforecast.model

import com.squareup.moshi.Json
import java.io.Serializable

data class Parameter(

    @Json(name = "localityName")
    val name: String,

    @Json(name = "levelType")
    val levelType: String,

    @Json(name = "level")
    val level: String,

    @Json(name = "unit")
    val unit: String,

    @Json(name = "values")
    val values: List<Double>
) : Serializable