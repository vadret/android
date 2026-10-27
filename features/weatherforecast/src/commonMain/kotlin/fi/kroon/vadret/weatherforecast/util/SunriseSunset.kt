package fi.kroon.vadret.weatherforecast.util

import kotlin.time.Instant

internal data class SunriseSunset(
    val sunrise: Instant,
    val sunset: Instant
)