package fi.kroon.vadret.warningfilter.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *  A warning area from SMHI's IBWW metadata. [code] tells
 *  counties from sea districts.
 */
@Serializable
internal data class DistrictDto(
    @SerialName(value = "id")
    val id: Int,
    @SerialName(value = "sv")
    val name: String,
    @SerialName(value = "code")
    val code: String
)