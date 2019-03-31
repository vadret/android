package fi.kroon.vadret.presentation.aboutapp.library

import android.os.Parcelable
import fi.kroon.vadret.data.library.model.Library
import kotlinx.android.parcel.Parcelize

object AboutAppLibraryView {

    sealed class Event {
        object OnInit : Event()
        class OnProjectUrlClick(val item: Library) : Event()
        class OnSourceUrlClick(val item: Library) : Event()
        class OnLicenseUrlClick(val item: Library) : Event()
    }

    data class State(
        val renderEvent: RenderEvent = RenderEvent.None
    )

    sealed class RenderEvent {
        object Init : RenderEvent()
        object None : RenderEvent()
        class DisplayLibrary(val list: List<Library>) : RenderEvent()
        class OpenUrl(val url: String?) : RenderEvent()
        class Error(val message: String) : RenderEvent()
    }

    @Parcelize
    data class StateParcel(val a: String) : Parcelable
}