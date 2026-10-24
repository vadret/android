package fi.kroon.vadret.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.theme.ThemeRepository
import fi.kroon.vadret.data.theme.model.Theme
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.onResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

private const val STOP_TIMEOUT_MILLIS = 5_000L

class MainViewModel @Inject constructor(
    themeRepository: ThemeRepository,
    coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    /**
     *  Null until the stored theme is read, so the app
     *  never flashes the wrong theme on start.
     */
    val theme: StateFlow<Theme?> = themeRepository
        .observe()
        .map { result: Either<Failure, Theme> ->
            result.onResult(
                { failure: Failure ->
                    Timber.e("Failed reading theme: $failure")
                    Theme.LIGHT
                },
                { theme: Theme ->
                    theme
                }
            )
        }
        .flowOn(context = coroutineDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = STOP_TIMEOUT_MILLIS),
            initialValue = null
        )
}