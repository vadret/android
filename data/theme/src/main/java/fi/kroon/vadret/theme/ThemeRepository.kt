package fi.kroon.vadret.theme

import android.content.SharedPreferences
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.theme.local.DefaultThemeLocalDataSource
import fi.kroon.vadret.theme.model.Theme
import io.github.sphrak.either.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {

    suspend fun getTheme(): Either<Failure, Theme>

    /**
     *  Emits the stored theme, then every change to it.
     */
    fun observeTheme(): Flow<Either<Failure, Theme>>

    suspend fun setTheme(theme: Theme): Either<Failure, Unit>
}

fun ThemeRepository(
    sharedPreferences: SharedPreferences,
    coroutineDispatcher: CoroutineDispatcher
): ThemeRepository =
    DefaultThemeRepository(
        themeLocalDataSource = DefaultThemeLocalDataSource(
            sharedPreferences = sharedPreferences,
            coroutineDispatcher = coroutineDispatcher
        )
    )