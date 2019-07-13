package fi.kroon.vadret.presentation.aboutapp

import fi.kroon.vadret.presentation.aboutapp.di.AboutAppFeatureScope
import fi.kroon.vadret.util.extension.asObservable
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import javax.inject.Inject

@AboutAppFeatureScope
class AboutAppViewModel @Inject constructor(
    private var state: AboutAppView.State
) {
    operator fun invoke(): ObservableTransformer<AboutAppView.Event,
        AboutAppView.State> = onEvent

    private val onEvent = ObservableTransformer<AboutAppView.Event,
        AboutAppView.State> { upstream: Observable<AboutAppView.Event> ->

        upstream.publish { shared: Observable<AboutAppView.Event> ->
            Observable.mergeArray(
                shared.ofType(AboutAppView.Event.OnViewInitialised::class.java)
            ).compose(
                eventToViewState
            )
        }
    }

    private val eventToViewState = ObservableTransformer<AboutAppView.Event,
        AboutAppView.State> { upstream: Observable<AboutAppView.Event> ->

        upstream.flatMap { event: AboutAppView.Event ->
            when (event) {
                AboutAppView.Event.OnViewInitialised ->
                    onInitialisedEvent()

                is AboutAppView.Event.OnTabSelected ->
                    state.asObservable()
            }
        }
    }

    private fun onInitialisedEvent(): Observable<AboutAppView.State> {
        state = state.copy(
            renderEvent = AboutAppView.RenderEvent.Initialised,
            isInitialised = true
        )
        return state.asObservable()
    }
}