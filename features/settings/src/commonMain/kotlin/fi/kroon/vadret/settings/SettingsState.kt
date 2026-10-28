package fi.kroon.vadret.settings

import fi.kroon.vadret.changelog.model.Changelog
import fi.kroon.vadret.core.resources.Res
import fi.kroon.vadret.core.resources.app_github_source_url
import fi.kroon.vadret.core.resources.app_license_url
import fi.kroon.vadret.core.resources.app_release_page
import fi.kroon.vadret.core.resources.changelog_row_title
import fi.kroon.vadret.core.resources.ic_chevron_right
import fi.kroon.vadret.core.resources.ic_code
import fi.kroon.vadret.core.resources.ic_copyright
import fi.kroon.vadret.core.resources.ic_history
import fi.kroon.vadret.core.resources.ic_info
import fi.kroon.vadret.core.resources.ic_open_in_new
import fi.kroon.vadret.core.resources.license_row_hint
import fi.kroon.vadret.core.resources.license_row_title
import fi.kroon.vadret.core.resources.source_code_row_title
import fi.kroon.vadret.core.resources.version_row_title
import fi.kroon.vadret.library.model.Library
import fi.kroon.vadret.settings.model.AboutRow
import fi.kroon.vadret.settings.model.AboutRowAction
import fi.kroon.vadret.settings.model.AboutRowHint
import fi.kroon.vadret.settings.model.LibraryGroup
import fi.kroon.vadret.settings.model.LocationRow
import fi.kroon.vadret.settings.ui.SettingsPage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

internal fun aboutRows(appVersion: String): ImmutableList<AboutRow> =
    persistentListOf(
        AboutRow(
            iconResource = Res.drawable.ic_info,
            titleResource = Res.string.version_row_title,
            hint = AboutRowHint.Plain(text = appVersion),
            action = AboutRowAction.OpenUrl(urlResource = Res.string.app_release_page),
            trailingIconResource = null
        ),
        AboutRow(
            iconResource = Res.drawable.ic_copyright,
            titleResource = Res.string.license_row_title,
            hint = AboutRowHint.Resource(resource = Res.string.license_row_hint),
            action = AboutRowAction.OpenUrl(urlResource = Res.string.app_license_url),
            trailingIconResource = Res.drawable.ic_chevron_right
        ),
        AboutRow(
            iconResource = Res.drawable.ic_history,
            titleResource = Res.string.changelog_row_title,
            hint = null,
            action = AboutRowAction.ShowChangelog,
            trailingIconResource = Res.drawable.ic_chevron_right
        ),
        AboutRow(
            iconResource = Res.drawable.ic_code,
            titleResource = Res.string.source_code_row_title,
            hint = null,
            action = AboutRowAction.OpenUrl(urlResource = Res.string.app_github_source_url),
            trailingIconResource = Res.drawable.ic_open_in_new
        )
    )

internal fun settingsPages(isDebug: Boolean): ImmutableList<SettingsPage> =
    SettingsPage.entries
        .filter { settingsPage: SettingsPage -> settingsPage != SettingsPage.DEVELOPER || isDebug }
        .toImmutableList()

internal data class SettingsState(
    val pages: ImmutableList<SettingsPage> = settingsPages(isDebug = false),
    val settings: Settings? = null,
    val locationRows: ImmutableList<LocationRow> = persistentListOf(),
    val libraryGroups: ImmutableList<LibraryGroup> = persistentListOf(),
    val selectedLibrary: Library? = null,
    val aboutRows: ImmutableList<AboutRow> = persistentListOf(),
    val isThemeDialogVisible: Boolean = false,
    val isLanguageDialogVisible: Boolean = false,
    val changelog: Changelog? = null,
    val isChangelogVisible: Boolean = false
)