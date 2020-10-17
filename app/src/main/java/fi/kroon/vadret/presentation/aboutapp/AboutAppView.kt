package fi.kroon.vadret.presentation.aboutapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

object AboutAppView {

    sealed class Event {
        object OnViewInitialised : Event()
        // class OnTabSelected(val position: Int) : Event()
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