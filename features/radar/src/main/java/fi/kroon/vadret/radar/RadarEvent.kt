package fi.kroon.vadret.radar

sealed interface RadarEvent {
    data object OnPlayButtonClicked : RadarEvent
    data object OnErrorShown : RadarEvent

    /**
     *  The user dragged the slider, which pauses playback.
     */
    data class OnIndexChanged(val index: Int) : RadarEvent
}