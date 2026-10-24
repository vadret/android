package fi.kroon.vadret.data.district.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *  A warning area from SMHI's IBWW metadata, either
 *  a county or a sea district.
 */
@Serializable
data class District(

    @SerialName("id")
    val id: Int,

    @SerialName("sv")
    val name: String,

    @SerialName("code")
    val code: String
)