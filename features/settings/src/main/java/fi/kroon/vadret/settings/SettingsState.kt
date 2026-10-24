package fi.kroon.vadret.settings

import fi.kroon.vadret.core.R
import fi.kroon.vadret.library.model.Library
import fi.kroon.vadret.settings.model.AboutRow
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private val defaultAboutRows: ImmutableList<AboutRow> = persistentListOf(
    AboutRow(
        iconResourceId = R.drawable.ic_info_outline,
        titleResourceId = R.string.version_row_title,
        hintResourceId = R.string.app_version,
        urlResourceId = R.string.app_release_page
    ),
    AboutRow(
        iconResourceId = R.drawable.ic_copyright,
        titleResourceId = R.string.license_row_title,
        hintResourceId = R.string.license_row_hint,
        urlResourceId = R.string.app_license_url
    ),
    AboutRow(
        iconResourceId = R.drawable.ic_history,
        titleResourceId = R.string.changelog_row_title,
        hintResourceId = null,
        urlResourceId = R.string.app_github_changelog_url
    ),
    AboutRow(
        iconResourceId = R.drawable.ic_code,
        titleResourceId = R.string.source_code_row_title,
        hintResourceId = null,
        urlResourceId = R.string.app_github_source_url
    )
)

data class SettingsState(
    val settings: Settings? = null,
    val libraries: ImmutableList<Library> = persistentListOf(),
    val aboutRows: ImmutableList<AboutRow> = defaultAboutRows,
    val isThemeDialogVisible: Boolean = false
)