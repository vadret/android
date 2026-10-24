package fi.kroon.vadret.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.library.model.Library
import fi.kroon.vadret.theme.model.Theme
import io.github.sphrak.either.extension.onResult
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class SettingsViewModel internal constructor(
    private val getSettingsTask: GetSettingsTask,
    private val setLocationModeTask: SetLocationModeTask,
    private val setThemeTask: SetThemeTask,
    private val getLibrariesTask: GetLibrariesTask,
    private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    val state: StateFlow<SettingsState>
        field = MutableStateFlow(value = SettingsState())

    private val isStarted: AtomicBoolean = AtomicBoolean(false)

    fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.OnStart -> onStart()
            is SettingsEvent.OnLocationModeToggled -> onLocationModeToggled(isAutomaticLocation = event.isAutomaticLocation)
            SettingsEvent.OnThemeRowClicked -> state.update { state: SettingsState ->
                state.copy(isThemeDialogVisible = true)
            }
            SettingsEvent.OnThemeDialogDismissed -> state.update { state: SettingsState ->
                state.copy(isThemeDialogVisible = false)
            }
            is SettingsEvent.OnThemeSelected -> onThemeSelected(theme = event.theme)
        }
    }

    private fun onStart() {
        if (isStarted.compareAndSet(false, true)) {
            viewModelScope.launch(context = coroutineDispatcher) {
                loadSettings()
                loadLibraries()
            }
        }
    }

    private suspend fun loadSettings() {
        getSettingsTask().onResult(
            onError = { _: Failure -> },
            onSuccess = { settings: Settings ->
                state.update { state: SettingsState ->
                    state.copy(settings = settings)
                }
            }
        )
    }

    private fun loadLibraries() {
        getLibrariesTask().onResult(
            onError = { },
            onSuccess = { libraries: List<Library> ->
                state.update { state: SettingsState ->
                    state.copy(libraries = libraries.toImmutableList())
                }
            }
        )
    }

    private fun onLocationModeToggled(isAutomaticLocation: Boolean) {
        viewModelScope.launch(context = coroutineDispatcher) {
            setLocationModeTask(isAutomaticLocation = isAutomaticLocation).onResult(
                onError = { _: Failure -> },
                onSuccess = { loadSettings() }
            )
        }
    }

    private fun onThemeSelected(theme: Theme) {
        state.update { state: SettingsState ->
            state.copy(isThemeDialogVisible = false)
        }
        viewModelScope.launch(context = coroutineDispatcher) {
            setThemeTask(theme = theme).onResult(
                onError = { _: Failure -> },
                onSuccess = { loadSettings() }
            )
        }
    }
}
