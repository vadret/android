package fi.kroon.vadret.presentation.aboutapp.library

import fi.kroon.vadret.data.library.model.Library

object AboutAppLibraryView {

    sealed class Event {
        object OnViewInitialised : Event()
        class OnProjectUrlClick(val item: Library) : Event()
        class OnSourceUrlClick(val item: Library) : Event()
        class OnLicenseUrlClick(val item: Library) : Event()
    }

    data class State(
        val renderEvent: RenderEvent = RenderEvent.None
    )

    sealed class RenderEvent {
        object Initialised : RenderEvent()
        object None : RenderEvent()
        class DisplayLibrary(val list: List<Library>) : RenderEvent()
        class OpenUrl(val url: String?) : RenderEvent()
    }
}