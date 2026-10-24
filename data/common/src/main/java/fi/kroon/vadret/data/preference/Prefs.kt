package fi.kroon.vadret.data.preference

import android.content.SharedPreferences

/**
 *  Typed access to the app's [SharedPreferences], which
 *  the settings screen also writes to.
 */
class Prefs(private val sharedPreferences: SharedPreferences) {

    fun string(key: String, defaultValue: String = ""): Pref<String> =
        Pref(
            sharedPreferences = sharedPreferences,
            key = key,
            read = { getString(key, defaultValue) ?: defaultValue },
            write = { value: String -> putString(key, value) }
        )

    fun long(key: String, defaultValue: Long = 0L): Pref<Long> =
        Pref(
            sharedPreferences = sharedPreferences,
            key = key,
            read = { getLong(key, defaultValue) },
            write = { value: Long -> putLong(key, value) }
        )

    fun boolean(key: String, defaultValue: Boolean = false): Pref<Boolean> =
        Pref(
            sharedPreferences = sharedPreferences,
            key = key,
            read = { getBoolean(key, defaultValue) },
            write = { value: Boolean -> putBoolean(key, value) }
        )
}