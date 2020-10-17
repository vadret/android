package fi.kroon.vadret.presentation.aboutapp.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.library.model.Library
import fi.kroon.vadret.domain.aboutapp.GetAboutLibraryTask
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
class AboutAppLibraryViewModel @Inject constructor(
    private val state: MutableSharedFlow<AboutAppLibraryView.State>,
    private var stateModel: AboutAppLibraryView.State,
    private val getAboutLibraryTask: GetAboutLibraryTask
) : ViewModel() {

    val viewState: SharedFlow<AboutAppLibraryView.State> get() = state.asSharedFlow()

    fun send(event: AboutAppLibraryView.Event) { viewModelScope.launch { reduce(event = event) } }

    private suspend fun reduce(event: AboutAppLibraryView.Event): Unit =
        when (event) {
            AboutAppLibraryView.Event.OnViewInitialised -> onViewInitialisedEvent()
            is AboutAppLibraryView.Event.OnProjectUrlClick -> onButtonClickedEvent(event.item.projectUrl)
            is AboutAppLibraryView.Event.OnSourceUrlClick -> onButtonClickedEvent(event.item.sourceUrl)
            is AboutAppLibraryView.Event.OnLicenseUrlClick -> onButtonClickedEvent(event.item.licenseUrl)
        }

    private suspend fun onButtonClickedEvent(url: String?) {
        stateModel = stateModel.copy(renderEvent = AboutAppLibraryView.RenderEvent.OpenUrl(url))
        state.emit(stateModel)
    }

    private suspend fun onViewInitialisedEvent() {
        getAboutLibraryTask()
            .await()
            .either(
                { failure: Failure ->
                    Timber.e("failure: $failure")
                    stateModel = stateModel.copy(renderEvent = AboutAppLibraryView.RenderEvent.None)
                    state.emit(stateModel)
                },
                { list: List<Library> ->
                    val renderEvent: AboutAppLibraryView.RenderEvent.DisplayLibrary =
                        AboutAppLibraryView.RenderEvent.DisplayLibrary(list)
                    stateModel = stateModel.copy(renderEvent = renderEvent)
                    state.emit(stateModel)
                }
            )
    }
}