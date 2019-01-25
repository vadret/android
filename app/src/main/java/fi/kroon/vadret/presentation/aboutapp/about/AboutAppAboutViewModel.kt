package fi.kroon.vadret.presentation.aboutapp.about

import android.content.Context
import fi.kroon.vadret.R
import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.domain.AboutInfoTask
import fi.kroon.vadret.utils.extensions.asObservable
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import javax.inject.Inject

class AboutAppAboutViewModel @Inject constructor(
    private var state: AboutAppAboutView.State,
    private val aboutInfoTask: AboutInfoTask,
    private val context: Context
) {
    operator fun invoke(): ObservableTransformer<AboutAppAboutView.Event, AboutAppAboutView.State> = onEvent

    private val onEvent = ObservableTransformer<AboutAppAboutView.Event,
        AboutAppAboutView.State> { upstream: Observable<AboutAppAboutView.Event> ->
        upstream.publish { shared: Observable<AboutAppAboutView.Event> ->
            Observable.mergeArray(
                shared.ofType(AboutAppAboutView.Event.OnInit::class.java),
                shared.ofType(AboutAppAboutView.Event.OnItemClick::class.java)
            ).compose(
                eventToViewState
            )
        }
    }
    // Transitioning
    private val eventToViewState = ObservableTransformer<AboutAppAboutView.Event,
        AboutAppAboutView.State> { upstream: Observable<AboutAppAboutView.Event> ->

        upstream.flatMap { event: AboutAppAboutView.Event ->
            when (event) {
                AboutAppAboutView.Event.OnInit ->
                    onInitEvent()

                is AboutAppAboutView.Event.OnItemClick ->
                    onItemClickEvent(event.item)
            }
        }
    }

    private fun onInitEvent(): Observable<AboutAppAboutView.State> =
        aboutInfoTask()
            .map { result: Either<Failure, List<AboutInfo>> ->

                result.either(
                    { _: Failure ->
                        state = state.copy(renderEvent = AboutAppAboutView.RenderEvent.None)

                        state
                    },
                    { list: List<AboutInfo> ->

                        val renderEvent: AboutAppAboutView.RenderEvent.DisplayInfo =
                            AboutAppAboutView.RenderEvent.DisplayInfo(list)

                        state = state.copy(renderEvent = renderEvent)

                        state
                    }
                )
            }.toObservable()

    private fun onItemClickEvent(item: AboutInfo): Observable<AboutAppAboutView.State> =
        when {

            item.titleResourceId == R.string.changelog_row_title -> {
                item.urlResourceId?.let {
                    val url: String = context.getString(item.urlResourceId)
                    state = state.copy(renderEvent = AboutAppAboutView.RenderEvent.OpenUrl(url))
                }
                state.asObservable()
            }
            // release
            item.urlResourceId == R.string.app_release_page -> {
                item.urlResourceId.let {
                    val url = context.getString(item.urlResourceId)
                    state = state.copy(renderEvent = AboutAppAboutView.RenderEvent.OpenUrl(url))
                }

                state.asObservable()
            }
            // sourceUrl
            item.titleResourceId == R.string.source_code_row_title -> {
                item.urlResourceId?.let {
                    val url = context.getString(item.urlResourceId)
                    state = state.copy(renderEvent = AboutAppAboutView.RenderEvent.OpenUrl(url))
                }

                state.asObservable()
            }

            // projectUrl
            item.urlResourceId != null -> {
                val url = context.getString(item.urlResourceId)
                state = state.copy(renderEvent = AboutAppAboutView.RenderEvent.OpenUrl(url))
                state.asObservable()
            }

            else -> {
                state = state.copy(renderEvent = AboutAppAboutView.RenderEvent.None)
                state.asObservable()
            }
        }
}