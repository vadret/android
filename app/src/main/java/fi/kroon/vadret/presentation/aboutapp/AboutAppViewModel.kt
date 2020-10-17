package fi.kroon.vadret.presentation.aboutapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
class AboutAppViewModel @Inject constructor(
    private val state: MutableSharedFlow<AboutAppView.State>,
    private var stateModel: AboutAppView.State,
) : ViewModel() {

    val viewState: SharedFlow<AboutAppView.State> get() = state.asSharedFlow()

    fun send(event: AboutAppView.Event) {
        viewModelScope
            .launch {
                reduce(event = event)
            }
    }

    private suspend fun reduce(event: AboutAppView.Event): Unit =
        when (event) {
            AboutAppView.Event.OnViewInitialised -> onInitialisedEvent()
            // is AboutAppView.Event.OnTabSelected -> TODO()
        }

    private suspend fun onInitialisedEvent() {
        stateModel = stateModel.copy(
            renderEvent = AboutAppView.RenderEvent.Initialised,
            isInitialised = true
        )
        state.emit(stateModel)
    }
}