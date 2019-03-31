package fi.kroon.vadret.presentation.aboutapp.library

import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.library.model.Library
import fi.kroon.vadret.domain.aboutapp.GetLibraryTask
import fi.kroon.vadret.utils.extensions.asObservable
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import javax.inject.Inject

class AboutAppLibraryViewModel @Inject constructor(
    private var state: AboutAppLibraryView.State,
    private val getLibraryTask: GetLibraryTask
) {
    operator fun invoke(): ObservableTransformer<AboutAppLibraryView.Event, AboutAppLibraryView.State> = onEvent

    private val onEvent = ObservableTransformer<AboutAppLibraryView.Event,
        AboutAppLibraryView.State> { upstream: Observable<AboutAppLibraryView.Event> ->
        upstream.publish { shared: Observable<AboutAppLibraryView.Event> ->
            Observable.mergeArray(
                shared.ofType(AboutAppLibraryView.Event.OnInit::class.java),
                shared.ofType(AboutAppLibraryView.Event.OnProjectUrlClick::class.java),
                shared.ofType(AboutAppLibraryView.Event.OnSourceUrlClick::class.java),
                shared.ofType(AboutAppLibraryView.Event.OnLicenseUrlClick::class.java)
            ).compose(
                eventToViewState
            )
        }
    }

    private val eventToViewState = ObservableTransformer<AboutAppLibraryView.Event,
        AboutAppLibraryView.State> { upstream: Observable<AboutAppLibraryView.Event> ->

        upstream.flatMap { event: AboutAppLibraryView.Event ->
            when (event) {
                AboutAppLibraryView.Event.OnInit ->
                    onInitEvent()
                is AboutAppLibraryView.Event.OnProjectUrlClick ->
                    onLibraryButtonClick(event.item.projectUrl)
                is AboutAppLibraryView.Event.OnSourceUrlClick ->
                    onLibraryButtonClick(event.item.sourceUrl)
                is AboutAppLibraryView.Event.OnLicenseUrlClick ->
                    onLibraryButtonClick(event.item.licenseUrl)
            }
        }
    }

    private fun onLibraryButtonClick(url: String?): Observable<AboutAppLibraryView.State> {
        state = state.copy(renderEvent = AboutAppLibraryView.RenderEvent.OpenUrl(url))
        return state.asObservable()
    }

    private fun onInitEvent(): Observable<AboutAppLibraryView.State> =
        getLibraryTask()
            .map { result: Either<Failure, List<Library>> ->

                result.either(
                    { _: Failure ->
                        state = state.copy(renderEvent = AboutAppLibraryView.RenderEvent.None)

                        state
                    },
                    { list: List<Library> ->
                        val renderEvent: AboutAppLibraryView.RenderEvent.DisplayLibrary =
                            AboutAppLibraryView.RenderEvent.DisplayLibrary(list)
                        state = state.copy(renderEvent = renderEvent)

                        state
                    }
                )
            }.toObservable()
}