package fi.kroon.vadret.settings

import fi.kroon.vadret.data.theme.model.Theme

sealed interface SettingsEvent {
    data class OnLocationModeToggled(val isGpsEnabled: Boolean) : SettingsEvent
    data object OnThemeRowClicked : SettingsEvent
    data object OnThemeDialogDismissed : SettingsEvent
    data class OnThemeSelected(val theme: Theme) : SettingsEvent
}