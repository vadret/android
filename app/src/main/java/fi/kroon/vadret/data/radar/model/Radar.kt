package fi.kroon.vadret.data.radar.model

import com.squareup.moshi.Json
import java.io.Serializable

data class Radar(

    @Json(name = "key")
    val key: String,

    @Json(name = "updated")
    val updated: String,

    @Json(name = "timeZone")
    val timeZone: String,

    @Json(name = "downloads")
    val downloads: List<Download>,

    @Json(name = "files")
    val files: List<File>
) : Serializable