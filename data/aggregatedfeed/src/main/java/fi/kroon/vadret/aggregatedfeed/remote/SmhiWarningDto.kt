package fi.kroon.vadret.aggregatedfeed.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *  An impact based weather warning from SMHI's IBWW API,
 *  issued for one or more [warningAreas].
 */
@Serializable
internal data class SmhiWarningDto(
    @SerialName(value = "id")
    val id: Int,
    @SerialName(value = "event")
    val event: SmhiTextDto,
    @SerialName(value = "descriptions")
    val descriptions: List<SmhiDescriptionDto>? = null,
    @SerialName(value = "warningAreas")
    val warningAreas: List<SmhiWarningAreaDto>? = null
)