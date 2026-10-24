package fi.kroon.vadret.data.nominatim.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Address(

    @Json(name = "road")
    val road: String,

    @Json(name = "village")
    val village: String?,

    @Json(name = "county")
    val county: String?,

    @Json(name = "hamlet")
    val hamlet: String?,

    @Json(name = "city")
    val city: String?,

    @Json(name = "state")
    val state: String?,

    @Json(name = "country")
    val country: String,

    @Json(name = "town")
    val town: String?,

    @Json(name = "country_code")
    val countryCode: String
) : Parcelable