package fi.kroon.vadret.main

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.theme.model.Theme
import fi.kroon.vadret.main.di.MainActivityScope
import fi.kroon.vadret.shared.IViewModel
import fi.kroon.vadret.theme.GetThemeModeTask
import fi.kroon.vadret.theme.ObserveThemeChangeTask
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.onResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

@MainActivityScope
class MainActivityViewModel @Inject constructor(
    private var state: MainActivityView.State,
    private val getThemeModeTask: GetThemeModeTask,
    private val observeThemeChangeTask: ObserveThemeChangeTask
) : IViewModel {

    /**
     *  Emits [MainActivityView.RenderEvent.RestartActivity] whenever
     *  the theme setting differs from the theme in use.
     */
    val viewState: Flow<MainActivityView.State>
        get() = observeThemeChangeTask()
            .map { result: Either<Failure, String> ->
                result.onResult(
                    { failure: Failure ->
                        Timber.e("Failure: $failure")
                        state = state.copy(renderEvent = MainActivityView.RenderEvent.None)
                    },
                    { theme: String ->
                        val renderEvent: MainActivityView.RenderEvent = if (theme != state.currentTheme) {
                            state = state.copy(currentTheme = theme)
                            MainActivityView.RenderEvent.RestartActivity
                        } else {
                            MainActivityView.RenderEvent.None
                        }
                        state = state.copy(renderEvent = renderEvent)
                    }
                )
                state
            }

    /**
     *  @Workaround
     *  This needs to be exposed
     *  to allow for checking/applying theme synchronously
     *  before setContentView has executed in [MainActivity] onCreate.
     */
    fun getThemeMode(): Either<Failure, Theme> = getThemeModeTask()
        .also { result: Either<Failure, Theme> ->
            result.onResult(
                {
                    Unit
                },
                { theme: Theme ->
                    if (state.currentTheme != theme.name) {
                        state = state.copy(currentTheme = theme.name)
                    }
                }
            )
        }
}