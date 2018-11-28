package fi.kroon.vadret.data.alert.model

import com.squareup.moshi.Json

data class EventCode(

    @Json(name = "valueName")
    val valueName: String,

    @Json(name = "value")
    val value: String
)