package fi.kroon.vadret.settings

import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.data.library.model.Library

data class SettingsState(
    val settings: Settings? = null,
    val libraries: List<Library> = emptyList(),
    val aboutRows: List<AboutInfo> = emptyList(),
    val isThemeDialogVisible: Boolean = false
)