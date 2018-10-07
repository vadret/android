package fi.kroon.vadret.data.nominatim.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address(

    @Json(name = "building")
    val building: String,

    @Json(name = "house_number")
    val houseNumber: String,

    @Json(name = "road")
    val road: String,

    @Json(name = "neighbourhood")
    val neighbourhood: String,

    @Json(name = "suburb")
    val suburb: String,

    @Json(name = "village")
    val village: String?,

    @Json(name = "county")
    val county: String?,

    @Json(name = "hamlet")
    val hamlet: String?,

    @Json(name = "city_district")
    val cityDistrict: String,

    @Json(name = "city")
    val city: String?,

    @Json(name = "state")
    val state: String?,

    @Json(name = "post_code")
    val postCode: String,

    @Json(name = "country")
    val country: String,

    @Json(name = "town")
    val town: String?,

    @Json(name = "country_code")
    val countryCode: String
) : Parcelable