package fi.kroon.vadret.radar

import fi.kroon.vadret.radar.model.RadarLayer
import java.util.Locale

sealed interface RadarEvent {
    data object OnStart : RadarEvent
    data class OnLocaleChanged(val locale: Locale) : RadarEvent
    data object OnPlayButtonClicked : RadarEvent
    data object OnErrorShown : RadarEvent
    data class OnIndexChanged(val index: Int) : RadarEvent
    data class OnLayerSelected(val layer: RadarLayer) : RadarEvent
}