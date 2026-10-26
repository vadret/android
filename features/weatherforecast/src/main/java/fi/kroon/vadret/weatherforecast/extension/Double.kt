package fi.kroon.vadret.weatherforecast.extension

import java.util.Locale
import kotlin.math.roundToInt

private const val TENTHS_PER_DEGREE = 10
private const val ONE_DECIMAL = "%.1f"

internal fun Double.toTemperatureText(): String {
    val tenths: Int = (this * TENTHS_PER_DEGREE).roundToInt()
    return if (tenths % TENTHS_PER_DEGREE == 0) {
        (tenths / TENTHS_PER_DEGREE).toString()
    } else {
        ONE_DECIMAL.format(Locale.ROOT, tenths.toDouble() / TENTHS_PER_DEGREE)
    }
}