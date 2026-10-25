package fi.kroon.vadret.weatherforecast.model

sealed interface SunTimes {

    data class RiseAndSet(
        val sunriseText: String,
        val sunsetText: String
    ) : SunTimes

    data object PolarNight : SunTimes

    data object MidnightSun : SunTimes
}