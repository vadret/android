package fi.kroon.vadret.data.aggregatedfeed.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *  [id] matches the district ids from SMHI's area metadata.
 */
@Serializable
data class SmhiAffectedArea(

    @SerialName("id")
    val id: Int,

    @SerialName("sv")
    val sv: String
)