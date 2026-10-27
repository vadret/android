package fi.kroon.vadret.wind.local

internal data class WindFrameEntity(
    val validAtEpochMillis: Long,
    val windSpeeds: List<Float>,
    val windFromDirections: List<Float>
)