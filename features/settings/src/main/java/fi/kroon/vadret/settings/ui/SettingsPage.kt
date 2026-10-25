package fi.kroon.vadret.settings.ui

import androidx.annotation.StringRes
import fi.kroon.vadret.core.R

internal enum class SettingsPage(
    @StringRes val titleResourceId: Int
) {
    PREFERENCES(titleResourceId = R.string.settings),
    LIBRARIES(titleResourceId = R.string.about_app_libraries_tab_title),
    ABOUT(titleResourceId = R.string.about_app_about_title)
}