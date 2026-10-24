package fi.kroon.vadret.weatherforecast.util

import java.util.Locale
import kotlin.math.pow

/**
 *  The formula only holds for these winds and temperatures.
 */
private val windSpeedRange: ClosedFloatingPointRange<Double> = 2.0..35.0
private val temperatureRange: ClosedFloatingPointRange<Double> = -40.0..10.0

private const val BASE = 13.12
private const val TEMPERATURE_FACTOR = 0.6215
private const val WIND_FACTOR = 13.956
private const val TEMPERATURE_WIND_FACTOR = 0.48669
private const val WIND_EXPONENT = 0.16
private const val ONE_DECIMAL = "%.1f"

internal object WindChill {

    /**
     *  How cold it feels, one decimal, or null when the wind or temperature
     *  is outside what the formula covers. With v in m/s and T in Celsius,
     *  T_eff = 13.12 + 0.6215 * T - 13.956 * v^0.16 + 0.48669 * T * v^0.16
     *
     *  Formula: https://www.smhi.se/kunskapsbanken/meteorologi/vindens-kyleffekt-1.259
     */
    fun calculate(
        temperatureCelcius: Double,
        windSpeedMeterPerSecond: Double
    ): String? {
        if (windSpeedMeterPerSecond !in windSpeedRange || temperatureCelcius !in temperatureRange) {
            return null
        }
        val windFactor: Double = windSpeedMeterPerSecond.pow(WIND_EXPONENT)
        val windChill: Double = BASE +
            TEMPERATURE_FACTOR * temperatureCelcius -
            WIND_FACTOR * windFactor +
            TEMPERATURE_WIND_FACTOR * temperatureCelcius * windFactor
        return ONE_DECIMAL.format(Locale.ROOT, windChill.toFloat())
    }
}