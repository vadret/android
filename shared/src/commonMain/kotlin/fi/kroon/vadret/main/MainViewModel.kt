package fi.kroon.vadret.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.theme.model.Theme
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.onResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.concurrent.atomics.AtomicBoolean

internal class MainViewModel(
    private val observeThemeTask: ObserveThemeTask,
    private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    val theme: StateFlow<Theme?>
        field = MutableStateFlow<Theme?>(value = null)

    private val isStarted: AtomicBoolean = AtomicBoolean(value = false)

    fun onEvent(event: MainEvent) {
        when (event) {
            MainEvent.OnStart -> onStart()
        }
    }

    private fun onStart() {
        if (isStarted.compareAndSet(expectedValue = false, newValue = true)) {
            viewModelScope.launch(context = coroutineDispatcher) {
                observeThemeTask().collect { result: Either<Failure, Theme> ->
                    theme.value = result.onResult(
                        onError = { _: Failure -> Theme.LIGHT },
                        onSuccess = { observedTheme: Theme -> observedTheme }
                    )
                }
            }
        }
    }
}