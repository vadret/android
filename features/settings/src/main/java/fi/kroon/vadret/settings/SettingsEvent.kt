package fi.kroon.vadret.settings

import fi.kroon.vadret.theme.model.Theme

sealed interface SettingsEvent {
    data object OnStart : SettingsEvent
    data class OnLocationModeToggled(val isAutomaticLocation: Boolean) : SettingsEvent
    data object OnThemeRowClicked : SettingsEvent
    data object OnThemeDialogDismissed : SettingsEvent
    data class OnThemeSelected(val theme: Theme) : SettingsEvent
}