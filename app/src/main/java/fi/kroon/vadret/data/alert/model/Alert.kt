package fi.kroon.vadret.data.alert.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import fi.kroon.vadret.data.common.SingleToArray
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Alert(
    @SingleToArray
    @Json(name = "alert")
    val warnings: List<Warning>?
) : Serializable