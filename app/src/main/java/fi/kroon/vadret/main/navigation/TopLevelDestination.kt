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

internal enum class TopLevelDestination(
    val key: NavKey,
    @DrawableRes
    val iconResourceId: Int,
    @DrawableRes
    val selectedIconResourceId: Int,
    @StringRes
    val labelResourceId: Int
) {
    WEATHER(
        key = WeatherKey,
        iconResourceId = R.drawable.ic_partly_cloudy_day,
        selectedIconResourceId = R.drawable.ic_partly_cloudy_day_filled,
        labelResourceId = R.string.weather
    ),
    WARNINGS(
        key = WarningKey,
        iconResourceId = R.drawable.ic_warning,
        selectedIconResourceId = R.drawable.ic_warning_filled,
        labelResourceId = R.string.warnings
    ),
    RADAR(
        key = RadarKey,
        iconResourceId = R.drawable.ic_radar,
        selectedIconResourceId = R.drawable.ic_radar_filled,
        labelResourceId = R.string.radar
    ),
    SETTINGS(
        key = SettingsKey,
        iconResourceId = R.drawable.ic_settings,
        selectedIconResourceId = R.drawable.ic_settings_filled,
        labelResourceId = R.string.settings
    )
}