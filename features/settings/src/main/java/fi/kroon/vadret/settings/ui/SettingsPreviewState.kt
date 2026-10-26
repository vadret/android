package fi.kroon.vadret.settings.ui

import fi.kroon.vadret.library.model.Library
import fi.kroon.vadret.location.model.StoredLocation
import fi.kroon.vadret.settings.Settings
import fi.kroon.vadret.settings.SettingsState
import fi.kroon.vadret.settings.extension.toLibraryGroups
import fi.kroon.vadret.settings.extension.toLocationRows
import fi.kroon.vadret.theme.model.Theme

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

internal fun settingsPreviewState(theme: Theme): SettingsState {
    val settings = Settings(
        isAutomaticLocation = false,
        theme = theme,
        storedLocation = previewLocation
    )
    return SettingsState(
        settings = settings,
        locationRows = settings.toLocationRows(),
        libraryGroups = previewLibraries.toLibraryGroups()
    )
}