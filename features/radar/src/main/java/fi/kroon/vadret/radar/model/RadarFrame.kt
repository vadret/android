package fi.kroon.vadret.radar.model

import fi.kroon.vadret.ui.model.ElapsedTime

internal data class RadarFrame(
    val imageUrl: String,
    val timeText: String,
    val weekday: String,
    val elapsedTime: ElapsedTime
)