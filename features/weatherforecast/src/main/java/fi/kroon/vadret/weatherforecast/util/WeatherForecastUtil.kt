package fi.kroon.vadret.weatherforecast.util

import fi.kroon.vadret.core.R
import fi.kroon.vadret.icons.ClearSky
import fi.kroon.vadret.icons.ClearSkyNight
import fi.kroon.vadret.icons.CloudySky
import fi.kroon.vadret.icons.Fog
import fi.kroon.vadret.icons.HalfClearSky
import fi.kroon.vadret.icons.HalfClearSkyNight
import fi.kroon.vadret.icons.HeavyRain
import fi.kroon.vadret.icons.HeavyRainShowers
import fi.kroon.vadret.icons.HeavySleet
import fi.kroon.vadret.icons.HeavySleetShowers
import fi.kroon.vadret.icons.HeavySnowShowers
import fi.kroon.vadret.icons.HeavySnowfall
import fi.kroon.vadret.icons.LightRain
import fi.kroon.vadret.icons.LightRainShowers
import fi.kroon.vadret.icons.LightSleet
import fi.kroon.vadret.icons.LightSleetShowers
import fi.kroon.vadret.icons.LightSnowShowers
import fi.kroon.vadret.icons.LightSnowfall
import fi.kroon.vadret.icons.ModerateRain
import fi.kroon.vadret.icons.ModerateRainShowers
import fi.kroon.vadret.icons.ModerateSleet
import fi.kroon.vadret.icons.ModerateSleetShowers
import fi.kroon.vadret.icons.ModerateSnowShowers
import fi.kroon.vadret.icons.ModerateSnowfall
import fi.kroon.vadret.icons.NearlyClearSky
import fi.kroon.vadret.icons.NearlyClearSkyNight
import fi.kroon.vadret.icons.Overcast
import fi.kroon.vadret.icons.Thunder
import fi.kroon.vadret.icons.Thunderstorm
import fi.kroon.vadret.icons.VadretIconGraphic
import fi.kroon.vadret.icons.VadretIcons
import fi.kroon.vadret.icons.VariableCloudiness
import fi.kroon.vadret.icons.VariableCloudinessNight
import fi.kroon.vadret.logging.Logger
import fi.kroon.vadret.logging.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(tag = "fi.kroon.vadret.weatherforecast")

internal object WeatherForecastUtil {

    fun getWeatherSymbolIcon(
        symbolCode: Int,
        isDaytime: Boolean
    ): VadretIconGraphic =
        when (symbolCode) {
            1 -> if (isDaytime) {
                VadretIcons.Weather.ClearSky
            } else {
                VadretIcons.Weather.ClearSkyNight
            }

            2 -> if (isDaytime) {
                VadretIcons.Weather.NearlyClearSky
            } else {
                VadretIcons.Weather.NearlyClearSkyNight
            }

            3 -> if (isDaytime) {
                VadretIcons.Weather.VariableCloudiness
            } else {
                VadretIcons.Weather.VariableCloudinessNight
            }

            4 -> if (isDaytime) {
                VadretIcons.Weather.HalfClearSky
            } else {
                VadretIcons.Weather.HalfClearSkyNight
            }

            5 -> VadretIcons.Weather.CloudySky

            6 -> VadretIcons.Weather.Overcast

            7 -> VadretIcons.Weather.Fog

            8 -> VadretIcons.Weather.LightRainShowers

            9 -> VadretIcons.Weather.ModerateRainShowers

            10 -> VadretIcons.Weather.HeavyRainShowers

            11 -> VadretIcons.Weather.Thunderstorm

            12 -> VadretIcons.Weather.LightSleetShowers

            13 -> VadretIcons.Weather.ModerateSleetShowers

            14 -> VadretIcons.Weather.HeavySleetShowers

            15 -> VadretIcons.Weather.LightSnowShowers

            16 -> VadretIcons.Weather.ModerateSnowShowers

            17 -> VadretIcons.Weather.HeavySnowShowers

            18 -> VadretIcons.Weather.LightRain

            19 -> VadretIcons.Weather.ModerateRain

            20 -> VadretIcons.Weather.HeavyRain

            21 -> VadretIcons.Weather.Thunder

            22 -> VadretIcons.Weather.LightSleet

            23 -> VadretIcons.Weather.ModerateSleet

            24 -> VadretIcons.Weather.HeavySleet

            25 -> VadretIcons.Weather.LightSnowfall

            26 -> VadretIcons.Weather.ModerateSnowfall

            27 -> VadretIcons.Weather.HeavySnowfall

            else -> {
                logger.warn { "Unknown weather symbol $symbolCode" }
                VadretIcons.Weather.Overcast
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