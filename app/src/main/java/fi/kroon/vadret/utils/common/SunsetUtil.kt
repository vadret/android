/*
 * Sunrise Sunset_DEL Calculator.
 * Copyright (C) 2013-2017 Carmen Alvarez
 * Copyright (C) 2019 Niclas Kron (Converted to kotlin)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * This is derived from the following project: https://github.com/caarmen/SunriseSunset/
 *
 */

package fi.kroon.vadret.utils.common

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

/**
 *
 * Provides methods to determine the sunrise, sunset, civil twilight,
 * nautical twilight, and astronomical twilight times of a given
 * location, or if it is currently day or night at a given location. <br>
 * Also provides methods to convert between Gregorian and Julian dates.<br>
 * The formulas used by this class are from the Wikipedia articles on Julian Day
 * and Sunrise Equation. <br>
 *
 * @author Carmen Alvarez
 * @see <a href="http://en.wikipedia.org/wiki/Julian_day">Julian Day on Wikipedia</a>
 * @see <a href="http://en.wikipedia.org/wiki/Sunrise_equation">Sunrise equation on Wikipedia</a>
 *
 * Code converted from Java to Kotlin, 2019-02-24 by Niclas Kron
 * https://github.com/caarmen/SunriseSunset/blob/master/library/src/main/java/ca/rmen/sunrisesunset/SunriseSunset.java
 */
object SunsetUtil {

    enum class DayPeriod {
        DAY,
        CIVIL_TWILIGHT,
        NAUTICAL_TWILIGHT,
        ASTRONOMICAL_TWILIGHT,
        NIGHT
    }

    const val SUN_ALTITUDE_SUNRISE_SUNSET: Double = -0.833
    const val SUN_ALTITUDE_CIVIL_TWILIGHT: Double = -6.0
    const val SUN_ALTITUDE_NAUTICAL_TWILIGHT: Double = -12.0
    const val SUN_ALTITUDE_ASTRONOMICAL_TWILIGHT: Double = -18.0

    private const val JULIAN_DATE_2000_01_01: Int = 2451545
    private const val CONST_0009: Double = 0.0009
    private const val CONST_360: Int = 360
    private const val MILLISECONDS_IN_DAY: Long = 60 * 60 * 24 * 1000

    /**
     * Intermediate variables used in the sunrise equation
     * @see [Sunrise equation on Wikipedia](http://en.wikipedia.org/wiki/Sunrise_equation)
     */
    private class SolarEquationVariables constructor(
        internal val n: Double,
        internal val m: Double,
        internal val lambda: Double,
        internal val jTransit: Double,
        internal val delta: Double
    )

    /**
     * Calculate the civil twilight time for the given date and given location.
     *
     * @param day The day for which to calculate civil twilight
     * @param latitude the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @return a two-element Gregorian Calendar array. The first element is the
     * civil twilight dawn, the second element is the civil twilight dusk.
     * This will return null if there is no civil twilight. (Ex: no twilight in Antarctica in December)
     */
    fun getCivilTwilight(
        day: Calendar,
        latitude: Double,
        longitude: Double
    ): Array<Calendar>? {
        return getSunriseSunset(day, latitude, longitude, SUN_ALTITUDE_CIVIL_TWILIGHT)
    }

    /**
     * Calculate the nautical twilight time for the given date and given location.
     *
     * @param day The day for which to calculate nautical twilight
     * @param latitude the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @return a two-element Gregorian Calendar array. The first element is the
     * nautical twilight dawn, the second element is the nautical twilight dusk.
     * This will return null if there is no nautical twilight. (Ex: no twilight in Antarctica in December)
     */
    fun getNauticalTwilight(/**/
        day: Calendar,
        latitude: Double,
        longitude: Double
    ): Array<Calendar>? {
        return getSunriseSunset(day, latitude, longitude, SUN_ALTITUDE_NAUTICAL_TWILIGHT)
    }

    /**
     * Calculate the astronomical twilight time for the given date and given location.
     *
     * @param day The day for which to calculate astronomical twilight
     * @param latitude the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @return a two-element Gregorian Calendar array. The first element is the
     * astronomical twilight dawn, the second element is the  astronomical twilight dusk.
     * This will return null if there is no astronomical twilight. (Ex: no twilight in Antarctica in December)
     */
    fun getAstronomicalTwilight(
        day: Calendar,
        latitude: Double,
        longitude: Double
    ): Array<Calendar>? {
        return getSunriseSunset(day, latitude, longitude, SUN_ALTITUDE_ASTRONOMICAL_TWILIGHT)
    }

    /**
     * Calculate the sunrise and SUNSET_UTIL times for the given date and given
     * location. This is based on the Wikipedia article on the Sunrise equation.
     *
     * @param day The day for which to calculate sunrise and SUNSET_UTIL
     * @param latitude the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @return a two-element Gregorian Calendar array. The first element is the
     * sunrise, the second element is the SUNSET_UTIL. This will return null if there is no sunrise or SUNSET_UTIL. (Ex: no sunrise in Antarctica in June)
     * @see [Sunrise equation on Wikipedia](http://en.wikipedia.org/wiki/Sunrise_equation)
     */
    fun getSunriseSunset(
        day: Calendar,
        latitude: Double,
        longitude: Double
    ): Array<Calendar>? {
        return getSunriseSunset(day, latitude, longitude, SUN_ALTITUDE_SUNRISE_SUNSET)
    }

    /**
     * Convert a Gregorian calendar date to a Julian date. Accuracy is to the
     * second.
     * <br>
     * This is based on the Wikipedia article for Julian day.
     *
     * @param gregorianDate Gregorian date in any time zone.
     * @return the Julian date for the given Gregorian date.
     * @see <a href="http://en.wikipedia.org/wiki/Julian_day#Converting_Julian_or_Gregorian_calendar_date_to_Julian_Day_Number">Converting to Julian day number on Wikipedia</a>
     */
    fun getJulianDate(gregorianDate: Calendar): Double {
        // Convert the date to the UTC time zone.
        val tzUTC = TimeZone.getTimeZone("UTC")
        val gregorianDateUTC = Calendar.getInstance(tzUTC)
        gregorianDateUTC.timeInMillis = gregorianDate.getTimeInMillis()
        // For the year (Y) astronomical year numbering is used, thus 1 BC is 0,
        // 2 BC is -1, and 4713 BC is -4712.
        val year = gregorianDateUTC.get(Calendar.YEAR)
        // The months (M) January to December are 1 to 12
        val month = gregorianDateUTC.get(Calendar.MONTH) + 1
        // D is the day of the month.
        val day = gregorianDateUTC.get(Calendar.DAY_OF_MONTH)
        val a = (14 - month) / 12
        val y = year + 4800 - a
        val m = month + 12 * a - 3

        val julianDay = day + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045
        val hour = gregorianDateUTC.get(Calendar.HOUR_OF_DAY)
        val minute = gregorianDateUTC.get(Calendar.MINUTE)
        val second = gregorianDateUTC.get(Calendar.SECOND)

        return (julianDay.toDouble() + (hour.toDouble() - 12) / 24 +
            minute.toDouble() / 1440 + second.toDouble() / 86400)
    }

    fun getGregorianDate(julianDate: Double): Calendar {

        val DAYS_PER_4000_YEARS: Int = 146097
        val DAYS_PER_CENTURY: Int = 36524
        val DAYS_PER_4_YEARS: Int = 1461
        val DAYS_PER_5_MONTHS: Int = 153

        // Let J = JD + 0.5: (note: this shifts the epoch back by one half day,
        // to start it at 00:00UTC, instead of 12:00 UTC);
        val J: Int = (julianDate + 0.5).toInt()

        // let j = J + 32044; (note: this shifts the epoch back to astronomical
        // year -4800 instead of the start of the Christian era in year AD 1 of
        // the proleptic Gregorian calendar).
        val j: Int = J + 32044

        // let g = j div 146097; let dg = j mod 146097;
        val g: Int = j / DAYS_PER_4000_YEARS
        val dg: Int = j % DAYS_PER_4000_YEARS

        // let c = (dg div 36524 + 1) * 3 div 4; let dc = dg - c * 36524;
        val c: Int = (dg / DAYS_PER_CENTURY + 1) * 3 / 4
        val dc: Int = dg - c * DAYS_PER_CENTURY

        // let b = dc div 1461; let db = dc mod 1461;
        val b: Int = dc / DAYS_PER_4_YEARS
        val db: Int = dc % DAYS_PER_4_YEARS

        // let a = (db div 365 + 1) * 3 div 4; let da = db - a * 365;
        val a: Int = (db / 365 + 1) * 3 / 4
        val da: Int = db - a * 365

        // let y = g * 400 + c * 100 + b * 4 + a; (note: this is the integer
        // number of full years elapsed since March 1, 4801 BC at 00:00 UTC);
        val y: Int = g * 400 + c * 100 + b * 4 + a

        // let m = (da * 5 + 308) div 153 - 2; (note: this is the integer number
        // of full months elapsed since the last March 1 at 00:00 UTC);
        val m: Int = (da * 5 + 308) / DAYS_PER_5_MONTHS - 2

        // let d = da -(m + 4) * 153 div 5 + 122; (note: this is the number of
        // days elapsed since day 1 of the month at 00:00 UTC, including
        // fractions of one day);
        val d: Int = da - (m + 4) * DAYS_PER_5_MONTHS / 5 + 122

        // let Y = y - 4800 + (m + 2) div 12;
        val year: Int = y - 4800 + (m + 2) / 12

        // let M = (m + 2) mod 12 + 1;
        val month: Int = (m + 2) % 12

        // let D = d + 1;
        val day: Int = d + 1

        // Apply the fraction of the day in the Julian date to the Gregorian
        // date.
        // Example: dayFraction = 0.717
        val dayFraction: Double = julianDate + 0.5 - J

        // Ex: 0.717*24 = 17.208 hours. We truncate to 17 hours.
        val hours = (dayFraction * 24).toInt()
        // Ex: 17.208 - 17 = 0.208 days. 0.208*60 = 12.48 minutes. We truncate
        // to 12 minutes.
        val minutes = ((dayFraction * 24 - hours) * 60.0).toInt()
        // Ex: 17.208*60 - (17*60 + 12) = 1032.48 - 1032 = 0.48 minutes. 0.48*60
        // = 28.8 seconds.
        // We round to 29 seconds.
        val seconds = (dayFraction * 24.0 * 3600.0 - (hours * 3600 + minutes * 60) + .5).toInt()

        // Create the gregorian date in UTC.
        val gregorianDateUTC = Calendar.getInstance(TimeZone
            .getTimeZone("UTC"))
        gregorianDateUTC.set(Calendar.YEAR, year)
        gregorianDateUTC.set(Calendar.MONTH, month)
        gregorianDateUTC.set(Calendar.DAY_OF_MONTH, day)
        gregorianDateUTC.set(Calendar.HOUR_OF_DAY, hours)
        gregorianDateUTC.set(Calendar.MINUTE, minutes)
        gregorianDateUTC.set(Calendar.SECOND, seconds)
        gregorianDateUTC.set(Calendar.MILLISECOND, 0)

        // Convert to a Gregorian date in the local time zone.
        val gregorianDate = Calendar.getInstance()
        gregorianDate.timeInMillis = gregorianDateUTC.timeInMillis
        return gregorianDate
    }

    /**
     * Return intermediate variables used for calculating sunrise, SUNSET_UTIL, and solar noon.
     *
     * @param day The day for which to calculate the ecliptic longitude and jTransit
     * @param longitude the longitude of the location in degrees (West is negative)
     * @return a 2-element array with the ecliptic longitude (lambda) as the first element, and solar transit (jTransit) as the second element
     * @see [Sunrise equation on Wikipedia](http://en.wikipedia.org/wiki/Sunrise_equation)
     */
    private fun getSolarEquationVariables(day: Calendar, longitudePositive: Double): SolarEquationVariables {

        val longitude: Double = longitudePositive.unaryMinus()

        // longitudeNegative = -longitude

        // Get the given date as a Julian date.
        val julianDate = getJulianDate(day)

        // Calculate current Julian cycle (number of days since 2000-01-01).
        val nStar: Double = (julianDate - JULIAN_DATE_2000_01_01.toDouble() - CONST_0009 -
            longitude / CONST_360)
        val n = Math.round(nStar).toDouble()

        // Approximate solar noon
        val jStar: Double = JULIAN_DATE_2000_01_01.toDouble() + CONST_0009 + longitude / CONST_360 + n
        // Solar mean anomaly
        val m = Math
            .toRadians((357.5291 + 0.98560028 * (jStar - JULIAN_DATE_2000_01_01)) % CONST_360)

        // Equation of center
        val c: Double = (1.9148 * Math.sin(m) + 0.0200 * Math.sin(2 * m) +
            0.0003 * Math.sin(3 * m))

        // Ecliptic longitude
        val lambda: Double = Math
            .toRadians((Math.toDegrees(m) + 102.9372 + c + 180.0) % CONST_360)

        // Solar transit (hour angle for solar noon)
        val jTransit: Double = jStar + 0.0053 * Math.sin(m) - 0.0069 * Math.sin(2 * lambda)

        // Declination of the sun.
        val delta: Double = Math.asin(Math.sin(lambda) * Math.sin(Math.toRadians(23.439)))

        return SolarEquationVariables(n, m, lambda, jTransit, delta)
    }

    /**
     * Calculate the sunrise and SUNSET_UTIL times for the given date, given
     * location, and sun altitude.
     * This is based on the Wikipedia article on the Sunrise equation.
     *
     * @param day The day for which to calculate sunrise and SUNSET_UTIL
     * @param latitude the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @param sunAltitude [the angle between the horizon and the center of the sun's disc.](http://en.wikipedia.org/wiki/Solar_zenith_angle#Solar_elevation_angle)
     * @return a two-element Gregorian Calendar array. The first element is the
     * sunrise, the second element is the SUNSET_UTIL. This will return null if there is no sunrise or SUNSET_UTIL. (Ex: no sunrise in Antarctica in June)
     * @see [Sunrise equation on Wikipedia](http://en.wikipedia.org/wiki/Sunrise_equation)
     */
    fun getSunriseSunset(
        day: Calendar,
        latitude: Double,
        longitudePositive: Double,
        sunAltitude: Double
    ): Array<Calendar>? {

        // var longitude: Double = longitude

        val solarEquationVariables = getSolarEquationVariables(day, longitudePositive)

        val longitude = longitudePositive.unaryMinus()

        val latitudeRad = Math.toRadians(latitude)

        // Hour angle
        val omega = Math.acos((Math.sin(Math.toRadians(sunAltitude)) - Math
            .sin(latitudeRad) * Math.sin(solarEquationVariables.delta)) / (Math.cos(latitudeRad) * Math.cos(solarEquationVariables.delta)))

        if (java.lang.Double.isNaN(omega)) {
            return null
        }

        // Sunset_DEL
        val jset = (JULIAN_DATE_2000_01_01.toDouble() +
            CONST_0009 +
            ((Math.toDegrees(omega) + longitude) / CONST_360 + solarEquationVariables.n + 0.0053 * Math.sin(solarEquationVariables.m) - 0.0069 * Math.sin(2 * solarEquationVariables.lambda)))

        // Sunrise
        val jrise = solarEquationVariables.jTransit - (jset - solarEquationVariables.jTransit)
        // Convert SUNSET_UTIL and sunrise to Gregorian dates, in UTC
        val gregRiseUTC = getGregorianDate(jrise)
        val gregSetUTC = getGregorianDate(jset)

        // Convert the SUNSET_UTIL and sunrise to the timezone of the day parameter
        val gregRise = Calendar.getInstance(day.timeZone)
        gregRise.timeInMillis = gregRiseUTC.timeInMillis
        val gregSet = Calendar.getInstance(day.timeZone)
        gregSet.timeInMillis = gregSetUTC.timeInMillis
        return arrayOf(gregRise, gregSet)
    }

    /**
     * Calculate the solar noon time for the given date and given location.
     * This is based on the Wikipedia article on the Sunrise equation.
     *
     * @param day The day for which to calculate sunrise and SUNSET_UTIL
     * @param latitude the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @return a Calendar with the time set to solar noon for the given day.
     * @see [Sunrise equation on Wikipedia](http://en.wikipedia.org/wiki/Sunrise_equation)
     */
    fun getSolarNoon(day: Calendar, latitude: Double, longitude: Double): Calendar? {
        val solarEquationVariables = getSolarEquationVariables(day, longitude)

        // Add a check for Antarctica in June and December (sun always down or up, respectively).
        // In this case, jTransit will be filled in, but we need to check the hour angle omega for
        // sunrise.
        // If there's no sunrise (omega is NaN), there's no solar noon.
        val latitudeRad = Math.toRadians(latitude)

        // Hour angle
        val omega = Math.acos((Math.sin(Math.toRadians(SUN_ALTITUDE_SUNRISE_SUNSET)) - Math
            .sin(latitudeRad) * Math.sin(solarEquationVariables.delta)) / (Math.cos(latitudeRad) * Math.cos(solarEquationVariables.delta)))

        if (java.lang.Double.isNaN(omega)) {
            return null
        }

        // Convert jTransit Gregorian dates, in UTC
        val gregNoonUTC = getGregorianDate(solarEquationVariables.jTransit)
        val gregNoon = Calendar.getInstance(day.timeZone)
        gregNoon.timeInMillis = gregNoonUTC.timeInMillis
        return gregNoon
    }

    /**
     * @param latitude the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @return true if it is currently day at the given location. This returns
     * true if the current time at the location is after the sunrise and
     * before the SUNSET_UTIL for that location.
     */
    fun isDay(latitude: Double, longitude: Double): Boolean {
        val now = Calendar.getInstance()
        return isDay(now, latitude, longitude)
    }

    /**
     * @param calendar a datetime
     * @param latitude the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @return true if it is day at the given location and given datetime. This returns
     * true if the given datetime at the location is after the sunrise and
     * before the SUNSET_UTIL for that location.
     */
    fun isDay(calendar: Calendar, latitude: Double, longitude: Double): Boolean {
        val sunriseSunset = getSunriseSunset(calendar, latitude, longitude)
        // In extreme latitudes, there may be no sunrise/SUNSET_UTIL time in summer or
        // winter, because it will be day or night 24 hours
        if (sunriseSunset == null) {
            val month = calendar.get(Calendar.MONTH) // Reminder: January = 0
            return if (latitude > 0) {
                // Always day at the north pole in June
                // Always night at the north pole in December
                month >= 3 && month <= 10
            } else {
                month < 3 || month > 10
            }
        }
        val sunrise = sunriseSunset[0]
        val sunset = sunriseSunset[1]
        return calendar.after(sunrise) && calendar.before(sunset)
    }

    /**
     * @param latitude the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @return true if it is night at the given location currently. This returns
     * true if the current time at the location is after the astronomical twilight dusk and
     * before the astronomical twilight dawn for that location.
     */
    fun isNight(latitude: Double, longitude: Double): Boolean {
        val now = Calendar.getInstance()
        return isNight(now, latitude, longitude)
    }

    /**
     * @param calendar a datetime
     * @param latitude the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @return true if it is night at the given location and datetime. This returns
     * true if the given datetime at the location is after the astronomical twilight dusk and before
     * the astronomical twilight dawn.
     */
    @SuppressLint("SimpleDateFormat")
    fun isNight(calendar: Calendar, latitude: Double, longitude: Double): Boolean {
        val astronomicalTwilight = getAstronomicalTwilight(calendar, latitude, longitude)
        if (astronomicalTwilight == null) {
            val month = calendar.get(Calendar.MONTH) // Reminder: January = 0
            return if (latitude > 0) {
                month < 3 || month > 10
            } else {
                // Always night at the south pole in June
                // Always day at the south pole in December
                month >= 3 && month <= 10
            }
        }
        val dawn = astronomicalTwilight[0]
        val dusk = astronomicalTwilight[1]
        val format = SimpleDateFormat("yyyy/MM/dd HH:mm:ss z")
        format.timeZone = calendar.timeZone
        return calendar.before(dawn) || calendar.after(dusk)
    }

    /**
     * @param latitude the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @return true if it is currently civil twilight at the current time at the given location.
     * This returns true if the current time at the location is between SUNSET_UTIL and civil twilight dusk
     * or between civil twilight dawn and sunrise.
     */
    fun isCivilTwilight(latitude: Double, longitude: Double): Boolean {
        val today = Calendar.getInstance()
        return isCivilTwilight(today, latitude, longitude)
    }

    /**
     * @param calendar the datetime for which to determine if it's civil twilight in the given location
     * @param latitude the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @return true if it is civil twilight at the given location and the given calendar.
     * This returns true if the given time at the location is between SUNSET_UTIL and civil twilight dusk
     * or between civil twilight dawn and sunrise.
     */
    fun isCivilTwilight(calendar: Calendar, latitude: Double, longitude: Double): Boolean {
        val sunriseSunset = getSunriseSunset(calendar, latitude, longitude)
            ?: return false
        val civilTwilight = getCivilTwilight(calendar, latitude, longitude)
            ?: return false

        return calendar.after(sunriseSunset[1]) && calendar.before(civilTwilight[1]) || calendar.after(civilTwilight[0]) && calendar.before(sunriseSunset[0])
    }

    /**
     * @param latitude the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @return true if it is currently nautical twilight at the current time at the given location.
     * This returns true if the current time at the location is between civil and nautical twilight dusk
     * or between nautical and civil twilight dawn.
     */
    fun isNauticalTwilight(latitude: Double, longitude: Double): Boolean {
        val today = Calendar.getInstance()
        return isNauticalTwilight(today, latitude, longitude)
    }

    /**
     * @param calendar the datetime for which to determine if it's nautical twilight in the given location
     * @param latitude the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @return true if it is nautical twilight at the given location and the given calendar.
     * This returns true if the given time at the location is between civil and nautical twilight dusk
     * or between nautical and civil twilight dawn.
     */
    fun isNauticalTwilight(calendar: Calendar, latitude: Double, longitude: Double): Boolean {
        val civilTwilight = getCivilTwilight(calendar, latitude, longitude)
            ?: return false
        val nauticalTwilight = getNauticalTwilight(calendar, latitude, longitude)
            ?: return false

        return calendar.after(civilTwilight[1]) && calendar.before(nauticalTwilight[1]) || calendar.after(nauticalTwilight[0]) && calendar.before(civilTwilight[0])
    }

    /**
     * @param latitude the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @return true if it is currently astronomical twilight at the current time at the given location.
     * This returns true if the current time at the location is between nautical and astronomical twilight dusk
     * or between astronomical and nautical twilight dawn.
     */
    fun isAstronomicalTwilight(latitude: Double, longitude: Double): Boolean {
        val today = Calendar.getInstance()
        return isAstronomicalTwilight(today, latitude, longitude)
    }

    /**
     * @param calendar the datetime for which to determine if it's astronomical twilight in the given location
     * @param latitude the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @return true if it is astronomical twilight at the given location and the given calendar.
     * This returns true if the given time at the location is between nautical and astronomical twilight dusk
     * or between astronomical and nautical twilight dawn.
     */
    fun isAstronomicalTwilight(calendar: Calendar, latitude: Double, longitude: Double): Boolean {
        val nauticalTwilight = getNauticalTwilight(calendar, latitude, longitude)
            ?: return false
        val astronomicalTwilight = getAstronomicalTwilight(calendar, latitude, longitude)
            ?: return false

        return calendar.after(nauticalTwilight[1]) && calendar.before(astronomicalTwilight[1]) || calendar.after(astronomicalTwilight[0]) && calendar.before(nauticalTwilight[0])
    }

    /**
     * @param latitude the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @return true if it is civil, nautical, or astronomical twilight currently at the given location.
     */
    fun isTwilight(latitude: Double, longitude: Double): Boolean {
        val today = Calendar.getInstance()
        return isTwilight(today, latitude, longitude)
    }

    /**
     * @param latitude the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @param calendar the given datetime to check for twilight
     * @return true if at the given location and calendar, it is civil, nautical, or astronomical twilight.
     */
    fun isTwilight(calendar: Calendar, latitude: Double, longitude: Double): Boolean {
        return (isCivilTwilight(calendar, latitude, longitude) ||
            isNauticalTwilight(calendar, latitude, longitude) ||
            isAstronomicalTwilight(calendar, latitude, longitude))
    }

    fun getDayPeriod(calendar: Calendar, latitude: Double, longitude: Double): DayPeriod {
        if (isDay(calendar, latitude, longitude)) return SunsetUtil.DayPeriod.DAY
        if (isCivilTwilight(calendar, latitude, longitude)) return SunsetUtil.DayPeriod.CIVIL_TWILIGHT
        if (isNauticalTwilight(calendar, latitude, longitude)) return SunsetUtil.DayPeriod.NAUTICAL_TWILIGHT
        if (isAstronomicalTwilight(calendar, latitude, longitude)) return SunsetUtil.DayPeriod.ASTRONOMICAL_TWILIGHT
        return if (isNight(calendar, latitude, longitude)) SunsetUtil.DayPeriod.NIGHT else SunsetUtil.DayPeriod.NIGHT
    }

    /**
     *
     * @param calendar the datetime for which to determine the day length
     * @param latitude the latitude of the location in degrees.
     * @param longitude the longitude of the location in degrees (West is negative)
     * @return the number of milliseconds between sunrise and SUNSET_UTIL.
     */
    fun getDayLength(calendar: Calendar, latitude: Double, longitude: Double): Long {
        val sunriseSunset = getSunriseSunset(calendar, latitude, longitude)
        if (sunriseSunset == null) {
            val month = calendar.get(Calendar.MONTH) // Reminder: January = 0
            return if (latitude > 0) {
                if (month >= 3 && month <= 10) {
                    MILLISECONDS_IN_DAY // Always day at the north pole in June
                } else {
                    0 // Always night at the north pole in December
                }
            } else {
                if (month >= 3 && month <= 10) {
                    0 // Always night at the south pole in June
                } else {
                    MILLISECONDS_IN_DAY // Always day at the south pole in December
                }
            }
        }
        return sunriseSunset[1].timeInMillis - sunriseSunset[0].timeInMillis
    }
}