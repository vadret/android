package fi.kroon.vadret.data.weatherforecast.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class TimeSerie(
    @Json(name = "validTime")
    val validTime: String,
    @Json(name = "parameters")
    val parameters: List<Parameter>
) : Serializable