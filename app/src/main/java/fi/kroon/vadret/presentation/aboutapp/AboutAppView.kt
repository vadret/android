package fi.kroon.vadret.presentation.aboutapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

object AboutAppView {

    sealed class Event {
        object OnInit : Event()
        class OnTabSelected(val position: Int) : Event()
    }

    data class State(
        val renderEvent: RenderEvent = RenderEvent.None
    )

    sealed class RenderEvent {
        object Init : RenderEvent()
        object None : RenderEvent()
        class Error(val message: String) : RenderEvent()
    }

    @Parcelize
    data class StateParcel(val a: String) : Parcelable
}