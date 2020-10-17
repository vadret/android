package fi.kroon.vadret.presentation.aboutapp.about

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.kroon.vadret.R
import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.domain.aboutapp.GetAboutInfoTask
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
class AboutAppAboutViewModel @Inject constructor(
    private val state: MutableSharedFlow<AboutAppAboutView.State>,
    private var stateModel: AboutAppAboutView.State,
    private val getAboutInfoTask: GetAboutInfoTask,
    private val context: Context
) : ViewModel() {

    val viewState: SharedFlow<AboutAppAboutView.State> get() = state.asSharedFlow()

    fun send(event: AboutAppAboutView.Event) { viewModelScope.launch { reduce(event = event) } }

    private suspend fun reduce(event: AboutAppAboutView.Event): Unit =
        when (event) {
            AboutAppAboutView.Event.OnViewInitialised -> onViewInitialisedEvent()
            is AboutAppAboutView.Event.OnItemClick -> onItemClickEvent(event.item)
        }

    private suspend fun onViewInitialisedEvent(): Unit =
        getAboutInfoTask()
            .await()
            .either(
                { _: Failure ->
                    stateModel = stateModel.copy(renderEvent = AboutAppAboutView.RenderEvent.None)
                    state.emit(stateModel)
                },
                { list: List<AboutInfo> ->

                    val renderEvent: AboutAppAboutView.RenderEvent.DisplayInfo =
                        AboutAppAboutView.RenderEvent.DisplayInfo(list)

                    stateModel = stateModel.copy(renderEvent = renderEvent)
                    state.emit(stateModel)
                }
            )

    private suspend fun onItemClickEvent(item: AboutInfo) {
        when {
            item.titleResourceId == R.string.changelog_row_title -> {
                item.urlResourceId?.let {
                    val url: String = context.getString(item.urlResourceId)
                    stateModel =
                        stateModel.copy(renderEvent = AboutAppAboutView.RenderEvent.OpenUrl(url))
                }
                state.emit(stateModel)
            }
            // release
            item.urlResourceId == R.string.app_release_page -> {
                item.urlResourceId.let {
                    val url = context.getString(item.urlResourceId)
                    stateModel =
                        stateModel.copy(renderEvent = AboutAppAboutView.RenderEvent.OpenUrl(url))
                }
                state.emit(stateModel)
            }
            // sourceUrl
            item.titleResourceId == R.string.source_code_row_title -> {
                item.urlResourceId?.let {
                    val url = context.getString(item.urlResourceId)
                    stateModel =
                        stateModel.copy(renderEvent = AboutAppAboutView.RenderEvent.OpenUrl(url))
                }

                state.emit(stateModel)
            }
            // projectUrl
            item.urlResourceId != null -> {
                val url = context.getString(item.urlResourceId)
                stateModel =
                    stateModel.copy(renderEvent = AboutAppAboutView.RenderEvent.OpenUrl(url))
                state.emit(stateModel)
            }

            else -> {
                stateModel = stateModel.copy(renderEvent = AboutAppAboutView.RenderEvent.None)
                state.emit(stateModel)
            }
        }
    }
}