package fi.kroon.vadret.nominatim.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AddressDto(
    @SerialName(value = "city")
    val city: String? = null,
    @SerialName(value = "hamlet")
    val hamlet: String? = null,
    @SerialName(value = "village")
    val village: String? = null
)