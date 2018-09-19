package fi.kroon.vadret.data.weather.model

import com.squareup.moshi.Json

data class Weather(

    @Json(name = "approvedTime")
    val approvedTime: String,

    @Json(name = "referenceTime")
    val referenceTime: String,

    @Json(name = "geometry")
    val geometry: Geometry,

    @Json(name = "timeSeries")
    val timeSeries: List<TimeSerie>
)