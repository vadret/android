package fi.kroon.vadret.data.district.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 *  A warning area from SMHI's IBWW metadata, either
 *  a county or a sea district.
 */
@JsonClass(generateAdapter = true)
data class District(

    @Json(name = "id")
    val id: Int,

    @Json(name = "sv")
    val name: String,

    @Json(name = "code")
    val code: String
)