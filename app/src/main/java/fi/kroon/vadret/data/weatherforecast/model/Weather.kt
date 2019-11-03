package fi.kroon.vadret.data.weatherforecast.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Weather(

    @Json(name = "approvedTime")
    val approvedTime: String,

    @Json(name = "referenceTime")
    val referenceTime: String,

    @Json(name = "geometry")
    val geometry: Geometry,

    @Json(name = "timeSeries")
    val timeSeries: List<TimeSerie>? = emptyList()

) : Serializable, Parcelable