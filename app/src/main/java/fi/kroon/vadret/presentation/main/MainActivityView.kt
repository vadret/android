package fi.kroon.vadret.presentation.main

import androidx.annotation.StringRes
import fi.kroon.vadret.util.LIGHT_THEME

object MainActivityView {

    sealed class Event {
        object OnViewInitialised : Event()
    }

    data class State(
        val renderEvent: RenderEvent = RenderEvent.None,
        val currentTheme: String = LIGHT_THEME
    )

    sealed class RenderEvent {
        object None : RenderEvent()
        object RestartActivity : RenderEvent()
        class DisplayError(@StringRes val errorCode: Int) : RenderEvent()
    }
}