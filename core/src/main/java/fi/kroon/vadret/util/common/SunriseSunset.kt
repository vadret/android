package fi.kroon.vadret.util.common

import kotlin.time.Instant

data class SunriseSunset(
    val sunrise: Instant,
    val sunset: Instant
)