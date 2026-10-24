package fi.kroon.vadret.data.preference

import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class Pref<T>(
    private val sharedPreferences: SharedPreferences,
    private val key: String,
    private val read: SharedPreferences.() -> T,
    private val write: SharedPreferences.Editor.(T) -> SharedPreferences.Editor
) {

    fun get(): T = sharedPreferences.read()

    fun set(value: T) {
        sharedPreferences
            .edit()
            .write(value)
            .apply()
    }

    /**
     *  Emits the current value, then every change to it.
     */
    fun observe(): Flow<T> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey: String? ->
            if (changedKey == key) {
                trySend(get())
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        send(get())
        awaitClose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
}