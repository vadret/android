package fi.kroon.vadret.settings.ui

import fi.kroon.vadret.core.resources.Res
import fi.kroon.vadret.core.resources.about_app_about_title
import fi.kroon.vadret.core.resources.about_app_libraries_tab_title
import fi.kroon.vadret.core.resources.developer_settings_title
import fi.kroon.vadret.core.resources.settings
import org.jetbrains.compose.resources.StringResource

internal enum class SettingsPage(
    val titleResource: StringResource
) {
    PREFERENCES(titleResource = Res.string.settings),
    LIBRARIES(titleResource = Res.string.about_app_libraries_tab_title),
    ABOUT(titleResource = Res.string.about_app_about_title),
    DEVELOPER(titleResource = Res.string.developer_settings_title)
}