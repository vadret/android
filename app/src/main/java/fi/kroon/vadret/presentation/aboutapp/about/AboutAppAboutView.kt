package fi.kroon.vadret.presentation.aboutapp.about

import android.os.Parcelable
import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import kotlinx.android.parcel.Parcelize

object AboutAppAboutView {

    sealed class Event {
        object OnInit : Event()
        class OnItemClick(val item: AboutInfo) : Event()
    }

    data class State(
        val renderEvent: RenderEvent = RenderEvent.None
    )

    sealed class RenderEvent {
        object Init : RenderEvent()
        object None : RenderEvent()
        class DisplayInfo(val list: List<AboutInfo>) : RenderEvent()
        class OpenUrl(val url: String) : RenderEvent()
        class Error(val message: String) : RenderEvent()
    }

    @Parcelize
    data class StateParcel(val a: String) : Parcelable
}