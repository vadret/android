package fi.kroon.vadret.data.alert.model

import com.squareup.moshi.Json
import fi.kroon.vadret.data.alert.adapter.SingleToArray

data class Alert(
    @SingleToArray
    @Json(name = "alert")
    val alert: List<Warning>
)