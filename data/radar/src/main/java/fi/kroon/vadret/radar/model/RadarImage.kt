package fi.kroon.vadret.radar.model

import kotlin.time.Instant

/**
 *  A composite radar image of Sweden showing the precipitation at [validAt].
 */
data class RadarImage(
    val imageUrl: String,
    val validAt: Instant
)