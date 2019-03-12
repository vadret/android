package fi.kroon.vadret.data.weatherforecast.model

import com.squareup.moshi.Json
import java.io.Serializable

data class TimeSerie(
    @Json(name = "validTime")
    val validTime: String,
    @Json(name = "parameters")
    val parameters: List<Parameter>
) : Serializable