package fi.kroon.vadret.aggregatedfeed.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *  [id] matches the district ids from SMHI's area metadata.
 */
@Serializable
internal data class SmhiAffectedAreaDto(
    @SerialName(value = "id")
    val id: Int
)