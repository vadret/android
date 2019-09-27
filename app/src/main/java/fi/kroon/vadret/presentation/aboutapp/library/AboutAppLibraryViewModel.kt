package fi.kroon.vadret.presentation.aboutapp.library

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.library.model.Library
import fi.kroon.vadret.domain.aboutapp.GetAboutLibraryTask
import fi.kroon.vadret.util.extension.asObservable
import io.github.sphrak.either.Either
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import javax.inject.Inject
import timber.log.Timber

class AboutAppLibraryViewModel @Inject constructor(
    private var state: AboutAppLibraryView.State,
    private val getAboutLibraryTask: GetAboutLibraryTask
) {
    operator fun invoke(): ObservableTransformer<AboutAppLibraryView.Event, AboutAppLibraryView.State> = onEvent

    private val onEvent = ObservableTransformer<AboutAppLibraryView.Event,
        AboutAppLibraryView.State> { upstream: Observable<AboutAppLibraryView.Event> ->
        upstream.publish { shared: Observable<AboutAppLibraryView.Event> ->
            Observable.mergeArray(
                shared.ofType(AboutAppLibraryView.Event.OnViewInitialised::class.java),
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
                AboutAppLibraryView.Event.OnViewInitialised ->
                    onViewInitialisedEvent()
                is AboutAppLibraryView.Event.OnProjectUrlClick ->
                    onLibraryButtonClick(event.item.projectUrl)
                is AboutAppLibraryView.Event.OnSourceUrlClick ->
                    onLibraryButtonClick(event.item.sourceUrl)
                is AboutAppLibraryView.Event.OnLicenseUrlClick ->
                    onLibraryButtonClick(event.item.licenseUrl)
            }
        }
    }

    private fun onLibraryButtonClick(url: String?): Observable<AboutAppLibraryView.State> { 3
        state = state.copy(renderEvent = AboutAppLibraryView.RenderEvent.OpenUrl(url))
        return state.asObservable()
    }

    private fun onViewInitialisedEvent(): Observable<AboutAppLibraryView.State> =
        getAboutLibraryTask()
            .flatMapObservable { result: Either<Failure, List<Library>> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("failure: $failure")
                        state = state.copy(renderEvent = AboutAppLibraryView.RenderEvent.None)
                        state.asObservable()
                    },
                    { list: List<Library> ->
                        val renderEvent: AboutAppLibraryView.RenderEvent.DisplayLibrary =
                            AboutAppLibraryView.RenderEvent.DisplayLibrary(list)
                        state = state.copy(renderEvent = renderEvent)
                        state.asObservable()
                    }
                )
            }
}