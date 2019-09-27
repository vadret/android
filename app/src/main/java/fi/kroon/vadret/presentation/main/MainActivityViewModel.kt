package fi.kroon.vadret.presentation.main

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.theme.model.Theme
import fi.kroon.vadret.domain.theme.GetThemeModeTask
import fi.kroon.vadret.domain.theme.ObserveThemeChangeTask
import fi.kroon.vadret.presentation.main.di.MainActivityScope
import fi.kroon.vadret.presentation.shared.IViewModel
import fi.kroon.vadret.util.extension.asObservable
import io.github.sphrak.either.Either
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import javax.inject.Inject
import timber.log.Timber

@MainActivityScope
class MainActivityViewModel @Inject constructor(
    private var state: MainActivityView.State,
    private val getThemeModeTask: GetThemeModeTask,
    private val observeThemeChangeTask: ObserveThemeChangeTask
) : IViewModel {

    operator fun invoke(): ObservableTransformer<MainActivityView.Event, MainActivityView.State> = onEvent

    private val onEvent = ObservableTransformer<MainActivityView.Event,
        MainActivityView.State> { upstream: Observable<MainActivityView.Event> ->
        upstream.publish { shared: Observable<MainActivityView.Event> ->
            Observable.mergeArray(
                shared.ofType(MainActivityView.Event.OnViewInitialised::class.java)
            ).compose(
                eventToViewState
            )
        }
    }

    private val eventToViewState = ObservableTransformer<MainActivityView.Event, MainActivityView.State> { upstream ->
        upstream.flatMap { event ->
            when (event) {
                MainActivityView.Event.OnViewInitialised -> onViewInitialisedEvent()
            }
        }
    }

    private fun onViewInitialisedEvent(): Observable<MainActivityView.State> = observeThemeChangeTask()
        .flatMap { result: Either<Failure, String> ->
            result.either(
                { failure: Failure ->
                    Timber.e("Failure: $failure")
                    state = state.copy(renderEvent = MainActivityView.RenderEvent.None)
                    state.asObservable()
                },
                { theme: String ->

                    val renderEvent: MainActivityView.RenderEvent = if (theme != state.currentTheme) {
                        state = state.copy(currentTheme = theme)
                        MainActivityView.RenderEvent.RestartActivity
                    } else {
                        MainActivityView.RenderEvent.None
                    }

                    state = state.copy(renderEvent = renderEvent)
                    state.asObservable()
                }
            )
        }

    /**
     *  @Workaround
     *  This needs to be exposed
     *  to allow for checking/applying theme synchronously
     *  before setContentView has executed in [MainActivity] onCreate.
     */
    fun getThemeMode(): Single<Either<Failure, Theme>> = getThemeModeTask()
        .map { result: Either<Failure, Theme> ->
            result.either(
                {
                    result
                },
                { theme: Theme ->
                    if (state.currentTheme != theme.name) {
                        state = state.copy(currentTheme = theme.name)
                    }
                    result
                }
            )
        }
}