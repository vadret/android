package fi.kroon.vadret.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.theme.model.Theme
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.onResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private const val STOP_TIMEOUT_MILLIS = 5_000L

class MainViewModel internal constructor(
    observeThemeTask: ObserveThemeTask,
    coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    val theme: StateFlow<Theme?> = observeThemeTask()
        .map { result: Either<Failure, Theme> ->
            result.onResult(
                onError = { _: Failure -> Theme.LIGHT },
                onSuccess = { theme: Theme -> theme }
            )
        }
        .flowOn(context = coroutineDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = STOP_TIMEOUT_MILLIS),
            initialValue = null
        )
}