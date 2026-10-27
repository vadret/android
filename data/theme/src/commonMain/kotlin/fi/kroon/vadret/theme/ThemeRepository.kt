package fi.kroon.vadret.theme

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.theme.local.DefaultThemeLocalDataSource
import fi.kroon.vadret.theme.model.Theme
import io.github.sphrak.either.Either
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    suspend fun getTheme(): Either<Failure, Theme>
    fun observeTheme(): Flow<Either<Failure, Theme>>
    suspend fun setTheme(theme: Theme): Either<Failure, Unit>
}

fun ThemeRepository(dataStore: DataStore<Preferences>): ThemeRepository =
    DefaultThemeRepository(
        themeLocalDataSource = DefaultThemeLocalDataSource(dataStore = dataStore)
    )