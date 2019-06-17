package fi.kroon.vadret.util.extension

import fi.kroon.vadret.util.MPS_TO_KMPH_FACTOR

fun Double.toCoordinate() = "%.6f".format(this).replace(",", ".").toDouble()
fun String.toCoordinate() = "%.6f".format(this.toDouble()).replace(",", ".").toDouble()
fun Double.toWindChill(wind: Double): String {

    /**
     *  If temperature is <= 10 we do this calculation
     *  Reference implementation: https://web.archive.org/web/20060427103553/
     *  http://www.msc.ec.gc.ca/education/windchill/science_equations_e.cfm
     */
    val temperature: Double = this
    val kmPh: Double = wind * MPS_TO_KMPH_FACTOR
    val windChill: Double = 13.12 + 0.6215 * temperature - 11.37 * Math.pow(kmPh, 0.16) + (0.3965 * temperature) * Math.pow(kmPh, 0.16)

    return "%.1f".format(windChill.toFloat()).replace(",", ".")
}