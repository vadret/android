package fi.kroon.vadret.data.district.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DistrictView(

    @Json(name = "id")
    val id: String,

    @Json(name = "district_view")
    val districtList: List<District>

)