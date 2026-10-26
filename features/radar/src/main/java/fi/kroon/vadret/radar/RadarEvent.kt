package fi.kroon.vadret.radar

import fi.kroon.vadret.radar.model.RadarLayer
import java.util.Locale

internal sealed interface RadarEvent {
    data object OnStart : RadarEvent
    data object OnMapLoaded : RadarEvent
    data class OnLocaleChanged(val locale: Locale) : RadarEvent
    data class OnDarkThemeChanged(val isDarkTheme: Boolean) : RadarEvent
    data object OnPlayButtonClicked : RadarEvent
    data object OnErrorShown : RadarEvent
    data class OnIndexChanged(val index: Int) : RadarEvent
    data class OnLayerSelected(val layer: RadarLayer) : RadarEvent
    data object OnRadarStationsToggled : RadarEvent
}