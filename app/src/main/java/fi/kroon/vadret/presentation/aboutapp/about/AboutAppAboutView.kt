package fi.kroon.vadret.presentation.aboutapp.about

import fi.kroon.vadret.data.aboutinfo.model.AboutInfo

object AboutAppAboutView {

    sealed class Event {
        object OnViewInitialised : Event()
        data class OnItemClick(val item: AboutInfo) : Event()
    }

    data class State(
        val renderEvent: RenderEvent = RenderEvent.None
    )

    sealed class RenderEvent {
        object Initialised : RenderEvent()
        object None : RenderEvent()
        class DisplayInfo(val list: List<AboutInfo>) : RenderEvent()
        class OpenUrl(val url: String) : RenderEvent()
    }
}