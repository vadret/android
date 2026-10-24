package fi.kroon.vadret.theme.local

import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

private const val THEME_KEY = "APP_THEME_MODE"

internal class DefaultThemeLocalDataSource(
    private val sharedPreferences: SharedPreferences,
    private val coroutineDispatcher: CoroutineDispatcher
) : ThemeLocalDataSource {

    override suspend fun getTheme(): String? =
        withContext(context = coroutineDispatcher) {
            readTheme()
        }

    override fun observeTheme(): Flow<String?> =
        callbackFlow {
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey: String? ->
                if (changedKey == THEME_KEY) {
                    trySend(element = readTheme())
                }
            }
            sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
            send(element = readTheme())
            awaitClose {
                sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }.flowOn(context = coroutineDispatcher)

    override suspend fun setTheme(value: String) {
        withContext(context = coroutineDispatcher) {
            sharedPreferences
                .edit()
                .putString(THEME_KEY, value)
                .apply()
        }
    }

    private fun readTheme(): String? =
        sharedPreferences.getString(THEME_KEY, null)
}