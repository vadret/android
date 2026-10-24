package fi.kroon.vadret.data.aggregatedfeed.model

import fi.kroon.vadret.util.extension.empty
import java.io.Serializable

data class Area(
    val type: String,
    val description: String,
    val coordinate: String? = String.empty()
) : Serializable