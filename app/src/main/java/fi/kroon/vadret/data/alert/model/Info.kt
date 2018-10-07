package fi.kroon.vadret.data.alert.model

import com.squareup.moshi.Json

data class Info(

    @Json(name = "language")
    val language: String,

    @Json(name = "category")
    val category: String,

    @Json(name = "event")
    val event: String,

    @Json(name = "urgency")
    val urgency: String,

    @Json(name = "severity")
    val severity: String,

    @Json(name = "certainty")
    val certainty: String,

    @Json(name = "eventCode")
    val eventCode: List<EventCode>,

    @Json(name = "effective")
    val effective: String,

    @Json(name = "onset")
    val onset: String,

    @Json(name = "expires")
    val expires: String,

    @Json(name = "senderName")
    val senderName: String,

    @Json(name = "headline")
    val headline: String,

    @Json(name = "description")
    val description: String,

    @Json(name = "web")
    val web: String,

    @Json(name = "parameter")
    val parameter: List<Parameter>,

    @Json(name = "area")
    val area: Area
)