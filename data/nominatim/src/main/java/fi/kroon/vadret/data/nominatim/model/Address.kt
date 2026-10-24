package fi.kroon.vadret.data.nominatim.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Address(

    @SerialName("road")
    val road: String?,

    @SerialName("village")
    val village: String?,

    @SerialName("county")
    val county: String?,

    @SerialName("hamlet")
    val hamlet: String?,

    @SerialName("city")
    val city: String?,

    @SerialName("state")
    val state: String?,

    @SerialName("country")
    val country: String,

    @SerialName("town")
    val town: String?,

    @SerialName("country_code")
    val countryCode: String
) : Parcelable