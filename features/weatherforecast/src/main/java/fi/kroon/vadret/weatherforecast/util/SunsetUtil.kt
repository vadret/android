package fi.kroon.vadret.weatherforecast.util

import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToLong
import kotlin.math.sin
import kotlin.time.Instant

/**
 *  Sunrise and sunset from the sunrise equation,
 *  see https://en.wikipedia.org/wiki/Sunrise_equation
 */
internal object SunsetUtil {

    private const val SUN_ALTITUDE_SUNRISE_SUNSET: Double = -0.833
    private const val EARTH_AXIAL_TILT_DEGREES: Double = 23.439
    private const val JULIAN_DATE_2000_01_01: Double = 2451545.0
    private const val JULIAN_DATE_UNIX_EPOCH: Double = 2440587.5
    private const val LEAP_SECONDS_DAYS: Double = 0.0009
    private const val DEGREES_IN_CIRCLE: Double = 360.0
    private const val DEGREES_IN_HALF_CIRCLE: Double = 180.0
    private const val MILLISECONDS_IN_DAY: Double = 86_400_000.0

    /**
     *  Null when the sun does not rise or set that day, as in
     *  polar night and midnight sun.
     */
    fun getSunriseSunset(
        day: Instant,
        latitude: Double,
        longitude: Double
    ): SunriseSunset? {
        val westLongitude: Double = -longitude
        val cycle: Double = floor(day.toJulianDate() - JULIAN_DATE_2000_01_01 - LEAP_SECONDS_DAYS - westLongitude / DEGREES_IN_CIRCLE + 0.5)
        val approximateSolarNoon: Double = JULIAN_DATE_2000_01_01 + LEAP_SECONDS_DAYS + westLongitude / DEGREES_IN_CIRCLE + cycle
        val meanAnomaly: Double = ((357.5291 + 0.98560028 * (approximateSolarNoon - JULIAN_DATE_2000_01_01)) % DEGREES_IN_CIRCLE).toRadians()
        val equationOfCenter: Double = 1.9148 * sin(meanAnomaly) + 0.0200 * sin(2 * meanAnomaly) + 0.0003 * sin(3 * meanAnomaly)
        val eclipticLongitude: Double = ((meanAnomaly.toDegrees() + 102.9372 + equationOfCenter + DEGREES_IN_HALF_CIRCLE) % DEGREES_IN_CIRCLE).toRadians()
        val solarTransit: Double = approximateSolarNoon + 0.0053 * sin(meanAnomaly) - 0.0069 * sin(2 * eclipticLongitude)
        val declination: Double = asin(sin(eclipticLongitude) * sin(EARTH_AXIAL_TILT_DEGREES.toRadians()))
        val latitudeRadians: Double = latitude.toRadians()
        val hourAngle: Double = acos(
            (sin(SUN_ALTITUDE_SUNRISE_SUNSET.toRadians()) - sin(latitudeRadians) * sin(declination)) /
                (cos(latitudeRadians) * cos(declination))
        )
        if (hourAngle.isNaN()) {
            return null
        }
        val sunset: Double = solarTransit + hourAngle.toDegrees() / DEGREES_IN_CIRCLE
        val sunrise: Double = solarTransit - hourAngle.toDegrees() / DEGREES_IN_CIRCLE
        return SunriseSunset(
            sunrise = sunrise.julianDateToInstant(),
            sunset = sunset.julianDateToInstant()
        )
    }

    private fun Instant.toJulianDate(): Double =
        toEpochMilliseconds() / MILLISECONDS_IN_DAY + JULIAN_DATE_UNIX_EPOCH

    private fun Double.julianDateToInstant(): Instant =
        Instant.fromEpochMilliseconds(((this - JULIAN_DATE_UNIX_EPOCH) * MILLISECONDS_IN_DAY).roundToLong())

    private fun Double.toRadians(): Double = this * PI / DEGREES_IN_HALF_CIRCLE

    private fun Double.toDegrees(): Double = this * DEGREES_IN_HALF_CIRCLE / PI
}