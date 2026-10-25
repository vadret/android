package fi.kroon.vadret.weatherforecast.util

import fi.kroon.vadret.core.R
import fi.kroon.vadret.logging.Logger
import fi.kroon.vadret.logging.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(tag = "fi.kroon.vadret.weatherforecast")

internal object WeatherForecastUtil {

    fun getWeatherSymbolIconResourceId(
        symbolCode: Int,
        isDaytime: Boolean
    ): Int =
        when (symbolCode) {
            1 -> if (isDaytime) {
                R.drawable.ic_clear_day
            } else {
                R.drawable.ic_clear_night
            }

            2, 3, 4 -> if (isDaytime) {
                R.drawable.ic_partly_cloudy_day
            } else {
                R.drawable.ic_partly_cloudy_night
            }

            5, 6 -> R.drawable.ic_cloud

            7 -> R.drawable.ic_foggy

            8, 18 -> R.drawable.ic_rainy_light

            9, 19 -> R.drawable.ic_rainy

            10, 20 -> R.drawable.ic_rainy_heavy

            11, 21 -> R.drawable.ic_thunderstorm

            12, 13, 14 -> R.drawable.ic_weather_mix

            22, 23, 24 -> R.drawable.ic_rainy_snow

            15, 25 -> R.drawable.ic_weather_snowy

            16, 26 -> R.drawable.ic_snowing

            17, 27 -> R.drawable.ic_snowing_heavy

            else -> {
                logger.warn { "Unknown weather symbol $symbolCode" }
                R.drawable.ic_cloud
            }
        }

    fun getWsymb2ResourceId(symbolCode: Int): Int =
        when (symbolCode) {
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
                logger.warn { "Wind direction outside 0 to 360: $degree" }
                R.string.empty_wind_direction
            }
        }
}