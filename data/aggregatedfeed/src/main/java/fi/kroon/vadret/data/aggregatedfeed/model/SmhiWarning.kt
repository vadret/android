package fi.kroon.vadret.data.aggregatedfeed.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *  An impact based weather warning from SMHI's IBWW API,
 *  issued for one or more [warningAreas].
 */
@Serializable
data class SmhiWarning(

    @SerialName("id")
    val id: Int,

    @SerialName("event")
    val event: SmhiText,

    @SerialName("descriptions")
    val descriptions: List<SmhiDescription>?,

    @SerialName("warningAreas")
    val warningAreas: List<SmhiWarningArea>?
)