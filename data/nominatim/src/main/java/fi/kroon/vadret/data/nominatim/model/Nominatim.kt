package fi.kroon.vadret.data.nominatim.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Nominatim(

    @SerialName("place_id")
    val placeId: Long,

    @SerialName("licence")
    val licence: String,

    @SerialName("osm_type")
    val osmType: String,

    @SerialName("osm_id")
    val osmId: Long,

    @SerialName("boundingbox")
    val boundingBox: List<String>,

    @SerialName("lat")
    val lat: String,

    @SerialName("lon")
    val lon: String,

    @SerialName("display_name")
    val displayName: String,

    @SerialName("address")
    val address: Address?,

    @SerialName("class")
    val klass: String?,

    @SerialName("type")
    val type: String?,

    @SerialName("importance")
    val importance: Double?,

    @SerialName("icon")
    val icon: String?

) : Parcelable