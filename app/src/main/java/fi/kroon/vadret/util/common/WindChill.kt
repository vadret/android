package fi.kroon.vadret.util.common

import kotlin.math.pow

object WindChill {

    /**
     *  Returns Wind chill as String if applicable or null
     *  T_eff = 13.12 + 0.6215 * T - 13.956 * v^0.16 + 0.48669 * T * v^0.16
     *  v is meter per second
     *  T is temperature in celcius
     *
     *  Formula: https://www.smhi.se/kunskapsbanken/meteorologi/vindens-kyleffekt-1.259
     */
    fun calculate(temperatureCelcius: Double, windSpeedMeterPerSecond: Double): String? {

        if (windSpeedMeterPerSecond < 2 || windSpeedMeterPerSecond > 35) return null
        if (temperatureCelcius > 10 || temperatureCelcius < -40) return null

        val v: Double = windSpeedMeterPerSecond.pow(0.16)
        val windChill: Double = 13.12 + 0.6215 * temperatureCelcius - 13.956 * v + (0.48669 * temperatureCelcius * v)

        return "%.1f".format(windChill.toFloat()).replace(",", ".")
    }
}