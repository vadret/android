package fi.kroon.vadret.settings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

object AboutAppView {

    sealed class Event {
        object OnViewInitialised : Event()
    }

    data class State(
        val isInitialised: Boolean = false,
        val renderEvent: RenderEvent = RenderEvent.None
    )

    sealed class RenderEvent {
        object Initialised : RenderEvent()
        object None : RenderEvent()
    }

    @Parcelize
    data class StateParcel(val a: String) : Parcelable
}