package fi.kroon.vadret.theme.local

import kotlinx.coroutines.flow.Flow

/**
 *  The theme as the preference screen of the old app stored it,
 *  null when nothing is stored.
 */
internal interface ThemeLocalDataSource {
    suspend fun getTheme(): String?
    fun observeTheme(): Flow<String?>
    suspend fun setTheme(value: String)
}