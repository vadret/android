package fi.kroon.vadret.data.radar.model

import com.squareup.moshi.Json

data class Download(

    @Json(name = "key")
    val key: String,

    @Json(name = "updated")
    val updated: String,

    @Json(name = "link")
    val link: String
)