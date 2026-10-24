package fi.kroon.vadret.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.library.model.Library
import fi.kroon.vadret.data.theme.model.Theme
import io.github.sphrak.either.extension.onResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val getSettingsTask: GetSettingsTask,
    private val setLocationModeTask: SetLocationModeTask,
    private val setThemeTask: SetThemeTask,
    private val getAboutInfoTask: GetAboutInfoTask,
    private val getAboutLibraryTask: GetAboutLibraryTask,
    private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val mutableState: MutableStateFlow<SettingsState> = MutableStateFlow(value = SettingsState())

    val state: StateFlow<SettingsState> = mutableState.asStateFlow()

    init {
        viewModelScope.launch(context = coroutineDispatcher) {
            loadSettings()
            loadLibraries()
            loadAboutRows()
        }
    }

    fun onEvent(event: SettingsEvent) {
        viewModelScope.launch(context = coroutineDispatcher) {
            when (event) {
                is SettingsEvent.OnLocationModeToggled -> onLocationModeToggled(isGpsEnabled = event.isGpsEnabled)
                SettingsEvent.OnThemeRowClicked -> mutableState.update { state -> state.copy(isThemeDialogVisible = true) }
                SettingsEvent.OnThemeDialogDismissed -> mutableState.update { state -> state.copy(isThemeDialogVisible = false) }
                is SettingsEvent.OnThemeSelected -> onThemeSelected(theme = event.theme)
            }
        }
    }

    private fun loadSettings() {
        getSettingsTask().onResult(
            onError = { failure: Failure -> Timber.e("Loading settings failed: $failure") },
            onSuccess = { settings: Settings -> mutableState.update { state -> state.copy(settings = settings) } }
        )
    }

    private suspend fun loadLibraries() {
        getAboutLibraryTask().onResult(
            onError = { failure: Failure -> Timber.e("Loading libraries failed: $failure") },
            onSuccess = { libraries: List<Library> -> mutableState.update { state -> state.copy(libraries = libraries) } }
        )
    }

    private suspend fun loadAboutRows() {
        getAboutInfoTask().onResult(
            onError = { failure: Failure -> Timber.e("Loading about rows failed: $failure") },
            onSuccess = { aboutRows: List<AboutInfo> -> mutableState.update { state -> state.copy(aboutRows = aboutRows) } }
        )
    }

    private fun onLocationModeToggled(isGpsEnabled: Boolean) {
        setLocationModeTask(isGpsEnabled = isGpsEnabled).onResult(
            onError = { failure: Failure -> Timber.e("Saving location mode failed: $failure") },
            onSuccess = { loadSettings() }
        )
    }

    private fun onThemeSelected(theme: Theme) {
        setThemeTask(theme = theme).onResult(
            onError = { failure: Failure -> Timber.e("Saving theme failed: $failure") },
            onSuccess = { loadSettings() }
        )
        mutableState.update { state -> state.copy(isThemeDialogVisible = false) }
    }
}