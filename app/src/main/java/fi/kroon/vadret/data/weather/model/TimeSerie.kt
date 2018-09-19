package fi.kroon.vadret.data.weather.model

import com.squareup.moshi.Json

data class TimeSerie(
    @Json(name = "validTime")
    val validTime: String,
    @Json(name = "parameters")
    val parameters: List<Parameter>
)