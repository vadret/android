package fi.kroon.vadret.data.aggregatedfeed.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 *  An impact based weather warning from SMHI's IBWW API,
 *  issued for one or more [warningAreas].
 */
@JsonClass(generateAdapter = true)
data class SmhiWarning(

    @Json(name = "id")
    val id: Int,

    @Json(name = "event")
    val event: SmhiText,

    @Json(name = "descriptions")
    val descriptions: List<SmhiDescription>?,

    @Json(name = "warningAreas")
    val warningAreas: List<SmhiWarningArea>?
)