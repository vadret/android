package fi.kroon.vadret.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.library.model.Library
import fi.kroon.vadret.theme.model.Theme
import io.github.sphrak.either.extension.onResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *  Failures are logged where they happen. The settings stay
 *  as they were shown, there is nothing the user can retry.
 */
class SettingsViewModel @Inject internal constructor(
    private val getSettingsTask: GetSettingsTask,
    private val setLocationModeTask: SetLocationModeTask,
    private val setThemeTask: SetThemeTask,
    private val getLibrariesTask: GetLibrariesTask,
    private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val mutableState: MutableStateFlow<SettingsState> = MutableStateFlow(value = SettingsState())
    val state: StateFlow<SettingsState> = mutableState.asStateFlow()

    init {
        viewModelScope.launch(context = coroutineDispatcher) {
            loadSettings()
            loadLibraries()
        }
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.OnLocationModeToggled -> onLocationModeToggled(isAutomaticLocation = event.isAutomaticLocation)
            SettingsEvent.OnThemeRowClicked -> mutableState.update { state: SettingsState ->
                state.copy(isThemeDialogVisible = true)
            }
            SettingsEvent.OnThemeDialogDismissed -> mutableState.update { state: SettingsState ->
                state.copy(isThemeDialogVisible = false)
            }
            is SettingsEvent.OnThemeSelected -> onThemeSelected(theme = event.theme)
        }
    }

    private suspend fun loadSettings() {
        getSettingsTask().onResult(
            onError = { _: Failure -> },
            onSuccess = { settings: Settings ->
                mutableState.update { state: SettingsState ->
                    state.copy(settings = settings)
                }
            }
        )
    }

    private fun loadLibraries() {
        getLibrariesTask().onResult(
            onError = { },
            onSuccess = { libraries: List<Library> ->
                mutableState.update { state: SettingsState ->
                    state.copy(libraries = libraries)
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
        mutableState.update { state: SettingsState ->
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