package fi.kroon.vadret.radar

import fi.kroon.vadret.radar.model.RadarLayer

sealed interface RadarEvent {
    data object OnStart : RadarEvent
    data object OnPlayButtonClicked : RadarEvent
    data object OnErrorShown : RadarEvent
    data class OnIndexChanged(val index: Int) : RadarEvent
    data class OnLayerSelected(val layer: RadarLayer) : RadarEvent
}