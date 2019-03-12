package fi.kroon.vadret.data.alert.model

import com.squareup.moshi.Json
import java.io.Serializable

data class EventCode(

    @Json(name = "valueName")
    val valueName: String,

    @Json(name = "value")
    val value: String
) : Serializable