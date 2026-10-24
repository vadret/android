package fi.kroon.vadret.radar.model

/**
 *  @param timeText local time the image shows, as "14:35".
 */
data class RadarFrame(
    val imageUrl: String,
    val timeText: String
)