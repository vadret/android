package fi.kroon.vadret.warningfilter.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class DistrictDto(
    @SerialName(value = "id")
    val id: Int,
    @SerialName(value = "sv")
    val name: String,
    @SerialName(value = "code")
    val code: String
)