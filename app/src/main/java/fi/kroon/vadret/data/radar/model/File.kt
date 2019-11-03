package fi.kroon.vadret.data.radar.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class File(

    @Json(name = "key")
    val key: String,

    @Json(name = "valid")
    val valid: String,

    @Json(name = "formats")
    val formats: List<Format>
) : Serializable