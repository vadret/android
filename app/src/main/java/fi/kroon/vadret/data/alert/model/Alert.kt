package fi.kroon.vadret.data.alert.model

import com.squareup.moshi.Json

data class Alert(

    @Json(name = "alert")
    val alert: List<Warning>
)