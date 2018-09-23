package fi.kroon.vadret.data.alert.model

import com.squareup.moshi.Json

data class Warning(

    @Json(name = "identifier")
    val identifier: String,

    @Json(name = "sender")
    val sender: String,

    @Json(name = "sent")
    val sent: String,

    @Json(name = "status")
    val status: String,

    @Json(name = "msgType")
    val msgType: String,

    @Json(name = "scope")
    val scope: String,

    @Json(name = "code")
    val code: List<String>,

    @Json(name = "info")
    val info: Info
)