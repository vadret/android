package fi.kroon.vadret.settings

import fi.kroon.vadret.changelog.model.Changelog
import fi.kroon.vadret.core.R
import fi.kroon.vadret.library.model.Library
import fi.kroon.vadret.settings.model.AboutRow
import fi.kroon.vadret.settings.model.AboutRowAction
import fi.kroon.vadret.settings.model.LibraryGroup
import fi.kroon.vadret.settings.model.LocationRow
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private val defaultAboutRows: ImmutableList<AboutRow> = persistentListOf(
    AboutRow(
        iconResourceId = R.drawable.ic_info,
        titleResourceId = R.string.version_row_title,
        hintResourceId = R.string.app_version,
        action = AboutRowAction.OpenUrl(urlResourceId = R.string.app_release_page),
        trailingIconResourceId = null
    ),
    AboutRow(
        iconResourceId = R.drawable.ic_copyright,
        titleResourceId = R.string.license_row_title,
        hintResourceId = R.string.license_row_hint,
        action = AboutRowAction.OpenUrl(urlResourceId = R.string.app_license_url),
        trailingIconResourceId = R.drawable.ic_chevron_right
    ),
    AboutRow(
        iconResourceId = R.drawable.ic_history,
        titleResourceId = R.string.changelog_row_title,
        hintResourceId = null,
        action = AboutRowAction.ShowChangelog,
        trailingIconResourceId = R.drawable.ic_chevron_right
    ),
    AboutRow(
        iconResourceId = R.drawable.ic_code,
        titleResourceId = R.string.source_code_row_title,
        hintResourceId = null,
        action = AboutRowAction.OpenUrl(urlResourceId = R.string.app_github_source_url),
        trailingIconResourceId = R.drawable.ic_open_in_new
    )
)

internal data class SettingsState(
    val settings: Settings? = null,
    val locationRows: ImmutableList<LocationRow> = persistentListOf(),
    val libraryGroups: ImmutableList<LibraryGroup> = persistentListOf(),
    val selectedLibrary: Library? = null,
    val aboutRows: ImmutableList<AboutRow> = defaultAboutRows,
    val isThemeDialogVisible: Boolean = false,
    val changelog: Changelog? = null,
    val isChangelogVisible: Boolean = false
)