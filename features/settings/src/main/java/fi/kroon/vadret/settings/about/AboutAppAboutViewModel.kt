package fi.kroon.vadret.settings.about

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.kroon.vadret.core.R
import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.settings.GetAboutInfoTask
import io.github.sphrak.either.extension.onResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

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
            .onResult(
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
        val urlResourceId: Int? = item.urlResourceId
        when {
            item.titleResourceId == R.string.changelog_row_title -> {
                urlResourceId?.let {
                    val url: String = context.getString(urlResourceId)
                    stateModel =
                        stateModel.copy(renderEvent = AboutAppAboutView.RenderEvent.OpenUrl(url))
                }
                state.emit(stateModel)
            }
            // release
            urlResourceId == R.string.app_release_page -> {
                urlResourceId.let {
                    val url = context.getString(urlResourceId)
                    stateModel =
                        stateModel.copy(renderEvent = AboutAppAboutView.RenderEvent.OpenUrl(url))
                }
                state.emit(stateModel)
            }
            // sourceUrl
            item.titleResourceId == R.string.source_code_row_title -> {
                urlResourceId?.let {
                    val url = context.getString(urlResourceId)
                    stateModel =
                        stateModel.copy(renderEvent = AboutAppAboutView.RenderEvent.OpenUrl(url))
                }
                state.emit(stateModel)
            }
            // projectUrl
            urlResourceId != null -> {
                val url = context.getString(urlResourceId)
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