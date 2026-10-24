package fi.kroon.vadret.radar

sealed interface RadarEvent {
    data object OnStart : RadarEvent
    data object OnPlayButtonClicked : RadarEvent
    data object OnErrorShown : RadarEvent
    data class OnIndexChanged(val index: Int) : RadarEvent
}