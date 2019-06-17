package fi.kroon.vadret.util.common

import fi.kroon.vadret.R
import timber.log.Timber

object WeatherForecastUtil {

    const val FIFTEEN_MINUTES = "15 min"
    const val THIRTY_MINUTES = "30 min"
    const val ONE_HOUR = "1 hour"
    const val THREE_HOURS = "3 hours"
    const val SIX_HOURS = "6 hours"
    const val TWELVE_HOURS = "12 hours"
    const val TWENTY_FOUR_HOURS = "24 hours"

    const val FIFTEEN_MINUTES_AS_MILLIS = 900_000L
    const val THIRTY_MINUTES_AS_MILLIS = 1_800_000L
    const val ONE_HOUR_AS_MILLIS = 3_600_000L
    const val THREE_HOURS_AS_MILLIS = 10_800_000L
    const val SIX_HOURS_AS_MILLIS = 21_600_000L
    const val TWELVE_HOURS_AS_MILLIS = 43_200_000L
    const val TWENTY_FOUR_HOURS_AS_MILLIS = 86_400_000L

    fun getPrecipitationResourceId(prSort: Int): Int =
        when (prSort) {
            0 -> R.string.prsort_no_precipitation
            1 -> R.string.prsort_snow
            2 -> R.string.prsort_snow_and_rain
            3 -> R.string.prsort_rain
            4 -> R.string.prsort_drizzle
            5 -> R.string.prsort_freezing_rain
            6 -> R.string.prsort_freezing_drizzle
            else -> R.string.prsort_no_precipitation
        }

    /**
     *  Calm winds: < 0.2 m/s
     *  Weak winds: 0.3 - 3.3 m/s
     *  Moderate winds: 3.4 - 7.9 m/s
     *  Fresh breezes: 8.0 - 13.8 m/s
     *  Strong winds: 13.9 - 24.4 m/s
     *  Storm winds: 24.5 - 32.6 m/s
     *  Hurricane: > 32.7 m/s
     */
    fun getWindSpeedClassResourceId(windSpeed: Double): Int = when {
        (windSpeed <= 0.2) -> R.string.ws_calm
        (windSpeed in 0.3..3.3) -> R.string.ws_weak
        (windSpeed in 3.4..7.9) -> R.string.ws_moderate
        (windSpeed in 8.0..13.8) -> R.string.ws_fresh
        (windSpeed in 13.9..24.4) -> R.string.ws_strong
        (windSpeed in 24.5..32.7) -> R.string.ws_windstorms
        else -> R.string.ws_hurricanes
    }

    fun getWsymb2IconResourceId(index: Int): Int =
        when (index) {
            1 -> R.drawable.wsymb2_clear_sky
            2 -> R.drawable.wsymb2_nearly_clear_sky
            3 -> R.drawable.wsymb2_variable_cloudiness
            4 -> R.drawable.wsymb2_halfclear_sky
            5 -> R.drawable.wsymb2_cloudy_sky
            6 -> R.drawable.wsymb2_overcast
            7 -> R.drawable.wsymb2_fog
            8 -> R.drawable.wsymb2_light_rain_showers
            9 -> R.drawable.wsymb2_moderate_rain_showers
            10 -> R.drawable.wsymb2_heavy_rain_showers
            11 -> R.drawable.wsymb2_thunderstorm
            12 -> R.drawable.wsymb2_light_sleet_showers
            13 -> R.drawable.wsymb2_moderate_sleet_showers
            14 -> R.drawable.wsymb2_heavy_sleet_showers
            15 -> R.drawable.wsymb2_light_snow_showers
            16 -> R.drawable.wsymb2_moderate_snow_showers
            17 -> R.drawable.wsymb2_heavy_snow_showers
            18 -> R.drawable.wsymb2_light_rain
            19 -> R.drawable.wsymb2_moderate_rain
            20 -> R.drawable.wsymb2_heavy_rain
            21 -> R.drawable.wsymb2_thunder
            22 -> R.drawable.wsymb2_light_sleet
            23 -> R.drawable.wsymb2_moderate_sleet
            24 -> R.drawable.wsymb2_heavy_sleet
            25 -> R.drawable.wsymb2_light_snowfall
            26 -> R.drawable.wsymb2_moderate_snowfall
            27 -> R.drawable.wsymb2_heavy_snowfall
            else -> {
                R.drawable.wsymb2_clear_sky
            }
        }

    fun getWsymb2ResourceId(index: Int): Int =
        when (index) {
            1 -> R.string.wsymb2_clear_sky
            2 -> R.string.wsymb2_nearly_clear_sky
            3 -> R.string.wsymb2_variable_cloudiness
            4 -> R.string.wsymb2_halfclear_sky
            5 -> R.string.wsymb2_cloudy_sky
            6 -> R.string.wsymb2_overcast
            7 -> R.string.wsymb2_fog
            8 -> R.string.wsymb2_light_rain_showers
            9 -> R.string.wsymb2_moderate_rain_showers
            10 -> R.string.wsymb2_heavy_rain_showers
            11 -> R.string.wsymb2_thunderstorm
            12 -> R.string.wsymb2_light_sleet_showers
            13 -> R.string.wsymb2_moderate_sleet_showers
            14 -> R.string.wsymb2_heavy_sleet_showers
            15 -> R.string.wsymb2_light_snow_showers
            16 -> R.string.wsymb2_moderate_snow_showers
            17 -> R.string.wsymb2_heavy_snow_showers
            18 -> R.string.wsymb2_light_rain
            19 -> R.string.wsymb2_moderate_rain
            20 -> R.string.wsymb2_heavy_rain
            21 -> R.string.wsymb2_thunder
            22 -> R.string.wsymb2_light_sleet
            23 -> R.string.wsymb2_moderate_sleet
            24 -> R.string.wsymb2_heavy_sleet
            25 -> R.string.wsymb2_light_snowfall
            26 -> R.string.wsymb2_moderate_snowfall
            27 -> R.string.wsymb2_heavy_snowfall
            else -> {
                R.string.wsymb2_clear_sky
            }
        }

    fun getTemperatureColorResourceId(temperature: Double): Int =
        when {

            // Range: < -10.0  -- Example: -22.0
            (-10.0 >= temperature) -> R.color.color_gradient_0

            // Range: -5.0 - -10.0  -- Example: -7.5
            (-5.0 >= temperature && temperature > -10.0) -> R.color.color_gradient_1

            // Range: -2.5 - -5.0   -- Example: -3.67
            (-2.5 >= temperature && temperature > -5.0) -> R.color.color_gradient_2

            // Range: -0.5 -> -2.5   -- Example: -1.75 || -0.5
            (-0.5 >= temperature && temperature > -2.5) -> R.color.color_gradient_3

            // Range: 0.0 -> -0.5    -- Example: -0.25 || 0.0
            (temperature <= 0.0 && -0.5 < temperature) -> R.color.color_gradient_4

            // Range: 0.0 -> 1.0     -- Example: 0.5 || 1.0
            (temperature <= 1.0 && 0.0 < temperature) -> R.color.color_gradient_5

            // Range: 1.0 -> 2.0     -- Example: 1.5 || 2.0
            (temperature <= 2.0 && 1.0 < temperature) -> R.color.color_gradient_6

            // Range: 2.0 -> 3.0     -- Example: 2.5 || 3.0
            (temperature <= 3.0 && 2.0 < temperature) -> R.color.color_gradient_7

            // Range: 3.0 -> 4.0     -- Example: 3.9 || 4.0
            (temperature <= 4.0 && 3.0 < temperature) -> R.color.color_gradient_8

            // Range: 4.0 -> 5.0     -- Example: 4.0 || 5.0
            (temperature <= 5.0 && 4.0 < temperature) -> R.color.color_gradient_9

            // Range: 5.0 -> 10.0    -- Example: 7.5 || 10.0
            (temperature <= 10.0 && 5.0 < temperature) -> R.color.color_gradient_10

            // Range: 10.0 -> 15.0   -- Example: 12.5 || 15.0
            (temperature <= 15.0 && 10.0 < temperature) -> R.color.color_gradient_11

            // Range: 15.0 -> 20.0   -- Example: 17.5 || 20.0
            (temperature <= 20.0 && 15.0 < temperature) -> R.color.color_gradient_12

            // Range: 20.0 -> 22.5   -- Example: 21.1 || 22.5
            (temperature <= 22.5 && 20.0 < temperature) -> R.color.color_gradient_13

            // Range: 22.5 -> 25.0   -- Example: 24.0 || 25.0
            (temperature <= 25.0 && 22.5 < temperature) -> R.color.color_gradient_14

            // Range: 25.0 - 30.0    -- Example: 27.0 ||  30.0
            (temperature <= 30.0 && 25.0 < temperature) -> R.color.color_gradient_15

            // Range: > 30.0         -- Example: 31.0 | 50.0
            (temperature > 30.0) -> R.color.color_gradient_16
            else -> R.color.color_gradient_NA
        }

    fun handleWindDirection(degree: Double): Int =
        when {

            // North: 0.0 - 11.24
            (degree <= 11.25 && 0.0 <= degree) -> R.string.north

            // North, North East: 11.25 - 33.74
            (degree <= 33.75 && 11.25 < degree) -> R.string.north_north_east

            // North East:  33.75 - 56.24
            (degree <= 56.25 && 33.75 < degree) -> R.string.north_east

            // East, North East:
            (degree <= 78.75 && 56.25 < degree) -> R.string.east_north_east

            // East:
            (degree <= 101.25 && 78.75 < degree) -> R.string.east

            // East, South East:
            (degree <= 123.75 && 101.25 < degree) -> R.string.east_south_east

            // South East
            (degree <= 146.25 && 123.75 < degree) -> R.string.south_east

            // South, South East
            (degree <= 168.75 && 146.25 < degree) -> R.string.south_south_east

            // South
            (degree <= 191.25 && 168.75 < degree) -> R.string.south

            // South, South West
            (degree <= 213.75 && 191.25 < degree) -> R.string.south_south_west

            // South West
            (degree <= 236.25 && 213.75 < degree) -> R.string.south_west

            // West, South West
            (degree <= 258.75 && 236.25 < degree) -> R.string.west_south_west

            // West
            (degree <= 281.25 && 258.75 < degree) -> R.string.west

            // West, North West
            (degree <= 303.75 && 281.25 < degree) -> R.string.west_north_west

            // North West
            (degree <= 326.25 && 303.75 < degree) -> R.string.north_west

            // North, North West
            (degree <= 348.75 && 326.25 < degree) -> R.string.north_north_west

            // North: 348.75 - 360.0
            (degree <= 360 && 348.75 < degree) -> R.string.north

            else -> {
                Timber.e("DisplayError: Wind direction is outside of range: $degree")
                R.string.empty_wind_direction
            }
        }

    fun getUpdateIntervalInMillis(period: String): Long =
        when (period) {
            FIFTEEN_MINUTES -> FIFTEEN_MINUTES_AS_MILLIS
            THIRTY_MINUTES -> THIRTY_MINUTES_AS_MILLIS
            ONE_HOUR -> ONE_HOUR_AS_MILLIS
            THREE_HOURS -> THREE_HOURS_AS_MILLIS
            SIX_HOURS -> SIX_HOURS_AS_MILLIS
            TWELVE_HOURS -> TWELVE_HOURS_AS_MILLIS
            TWENTY_FOUR_HOURS -> TWENTY_FOUR_HOURS_AS_MILLIS
            else -> FIFTEEN_MINUTES_AS_MILLIS
        }
}