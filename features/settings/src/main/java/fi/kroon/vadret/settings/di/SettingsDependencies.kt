package fi.kroon.vadret.settings.di

import fi.kroon.vadret.library.LibraryRepository
import fi.kroon.vadret.location.LocationRepository
import fi.kroon.vadret.theme.ThemeRepository
import kotlinx.coroutines.CoroutineDispatcher

/**
 *  What the settings screen needs from the app.
 */
interface SettingsDependencies {
    fun libraryRepository(): LibraryRepository
    fun locationRepository(): LocationRepository
    fun themeRepository(): ThemeRepository
    fun coroutineDispatcher(): CoroutineDispatcher
}