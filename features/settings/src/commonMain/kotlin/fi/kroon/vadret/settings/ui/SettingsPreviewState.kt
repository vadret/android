package fi.kroon.vadret.settings.ui

import fi.kroon.vadret.changelog.model.Changelog
import fi.kroon.vadret.library.model.Library
import fi.kroon.vadret.location.model.StoredLocation
import fi.kroon.vadret.settings.Settings
import fi.kroon.vadret.settings.SettingsState
import fi.kroon.vadret.settings.aboutRows
import fi.kroon.vadret.settings.extension.toLibraryGroups
import fi.kroon.vadret.settings.extension.toLocationRows
import fi.kroon.vadret.theme.model.Theme

private const val PREVIEW_APP_VERSION = "3.1.0"

private val previewLocation = StoredLocation(
    locality = "Stockholm",
    municipality = "Stockholm",
    county = "Stockholms län",
    latitude = 59.3293,
    longitude = 18.0686
)

private val previewLibraries: List<Library> = listOf(
    Library(
        author = "JetBrains",
        title = "Kotlin",
        description = "Statically typed programming language",
        projectUrl = "https://kotlinlang.org",
        sourceUrl = "https://github.com/JetBrains/kotlin",
        licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
        license = "Apache 2.0"
    ),
    Library(
        author = "Google",
        title = "Jetpack Compose",
        description = "Modern toolkit for building native Android UI",
        projectUrl = "https://developer.android.com/compose",
        sourceUrl = "https://android.googlesource.com/platform/frameworks/support",
        licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
        license = "Apache 2.0"
    )
)

private const val PREVIEW_CHANGELOG = """# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [3.1.0] -- 2026-10-26

### Added

- New 'Coming days' view

### Changed

- Interpolate between wind and precipitation frames
"""

internal fun changelogPreview(): Changelog = Changelog(markdown = PREVIEW_CHANGELOG)

internal fun settingsPreviewState(theme: Theme): SettingsState {
    val settings = Settings(
        isAutomaticLocation = false,
        theme = theme,
        storedLocation = previewLocation
    )
    return SettingsState(
        settings = settings,
        locationRows = settings.toLocationRows(),
        libraryGroups = previewLibraries.toLibraryGroups(),
        aboutRows = aboutRows(appVersion = PREVIEW_APP_VERSION)
    )
}