package fi.kroon.vadret.presentation.aboutapp

import fi.kroon.vadret.presentation.aboutapp.di.AboutAppScope
import fi.kroon.vadret.utils.extensions.asObservable
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import javax.inject.Inject

@AboutAppScope
class AboutAppViewModel @Inject constructor(
    private var state: AboutAppView.State
) {
    operator fun invoke(): ObservableTransformer<AboutAppView.Event,
        AboutAppView.State> = onEvent

    private val onEvent = ObservableTransformer<AboutAppView.Event,
        AboutAppView.State> { upstream: Observable<AboutAppView.Event> ->

        upstream.publish { shared: Observable<AboutAppView.Event> ->
            Observable.mergeArray(
                shared.ofType(AboutAppView.Event.OnInit::class.java)
            ).compose(
                eventToViewState
            )
        }
    }

    // Transition
    private val eventToViewState = ObservableTransformer<AboutAppView.Event,
        AboutAppView.State> { upstream: Observable<AboutAppView.Event> ->

        upstream.flatMap { event: AboutAppView.Event ->
            when (event) {
                AboutAppView.Event.OnInit ->
                    onInitEvent()

                is AboutAppView.Event.OnTabSelected ->
                    state.asObservable()
            }
        }
    }

    // Reducer
    private fun onInitEvent(): Observable<AboutAppView.State> {
        state = state.copy(renderEvent = AboutAppView.RenderEvent.Init)
        return state.asObservable()
    }
}