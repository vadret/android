package fi.kroon.vadret.main.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation3.runtime.NavKey
import fi.kroon.vadret.core.R
import kotlinx.serialization.Serializable

@Serializable
data object WeatherKey : NavKey

@Serializable
data object WarningKey : NavKey

@Serializable
data object RadarKey : NavKey

@Serializable
data object SettingsKey : NavKey

/**
 *  The tabs of the bottom bar, in order.
 */
internal enum class TopLevelDestination(
    val key: NavKey,
    @DrawableRes
    val iconResourceId: Int,
    @StringRes
    val labelResourceId: Int
) {
    WEATHER(
        key = WeatherKey,
        iconResourceId = R.drawable.wsymb2_weather,
        labelResourceId = R.string.weather
    ),
    WARNINGS(
        key = WarningKey,
        iconResourceId = R.drawable.wsymb2_warning,
        labelResourceId = R.string.warnings
    ),
    RADAR(
        key = RadarKey,
        iconResourceId = R.drawable.wsymb2_radar,
        labelResourceId = R.string.radar
    ),
    SETTINGS(
        key = SettingsKey,
        iconResourceId = R.drawable.wsymb2_settings,
        labelResourceId = R.string.settings
    )
}