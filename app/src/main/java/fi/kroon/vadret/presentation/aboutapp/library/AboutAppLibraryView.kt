package fi.kroon.vadret.presentation.aboutapp.library

import android.os.Parcelable
import fi.kroon.vadret.data.library.local.LibraryEntity
import kotlinx.android.parcel.Parcelize

object AboutAppLibraryView {

    sealed class Event {
        object OnInit : Event()
        class OnProjectUrlClick(val item: LibraryEntity) : Event()
        class OnSourceUrlClick(val item: LibraryEntity) : Event()
        class OnLicenseUrlClick(val item: LibraryEntity) : Event()
    }

    data class State(
        val renderEvent: RenderEvent = RenderEvent.None
    )

    sealed class RenderEvent {
        object Init : RenderEvent()
        object None : RenderEvent()
        class DisplayLibrary(val list: List<LibraryEntity>) : RenderEvent()
        class OpenUrl(val url: String) : RenderEvent()
        class Error(val message: String) : RenderEvent()
    }

    @Parcelize
    data class StateParcel(val a: String) : Parcelable
}