package fi.kroon.vadret.settings

import fi.kroon.vadret.library.model.Library
import fi.kroon.vadret.theme.model.Theme

internal sealed interface SettingsEvent {
    data object OnStart : SettingsEvent
    data class OnLocationModeToggled(val isAutomaticLocation: Boolean) : SettingsEvent
    data object OnThemeRowClicked : SettingsEvent
    data object OnThemeDialogDismissed : SettingsEvent
    data class OnThemeSelected(val theme: Theme) : SettingsEvent
    data class OnLibraryClicked(val library: Library) : SettingsEvent
    data object OnLibraryDismissed : SettingsEvent
    data object OnChangelogClicked : SettingsEvent
    data object OnChangelogDismissed : SettingsEvent
}