package fi.kroon.vadret.main

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.theme.model.Theme
import fi.kroon.vadret.main.di.MainActivityScope
import fi.kroon.vadret.shared.IViewModel
import fi.kroon.vadret.theme.GetThemeModeTask
import fi.kroon.vadret.theme.ObserveThemeChangeTask
import fi.kroon.vadret.util.extension.asObservable
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.onResult
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

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
            result.onResult(
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
            result.onResult(
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