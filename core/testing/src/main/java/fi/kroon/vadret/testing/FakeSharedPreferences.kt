package fi.kroon.vadret.testing

import android.content.SharedPreferences
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

/**
 *  In memory preferences. Edits apply at once and notify
 *  listeners the way the framework does after apply().
 */
class FakeSharedPreferences : SharedPreferences {

    private val values: ConcurrentHashMap<String, Any> = ConcurrentHashMap()
    private val listeners: CopyOnWriteArraySet<SharedPreferences.OnSharedPreferenceChangeListener> = CopyOnWriteArraySet()

    val listenerCount: Int
        get() = listeners.size

    override fun getAll(): Map<String, *> = values.toMap()

    override fun getString(
        key: String,
        defValue: String?
    ): String? = values[key] as? String ?: defValue

    @Suppress("UNCHECKED_CAST")
    override fun getStringSet(
        key: String,
        defValues: Set<String>?
    ): Set<String>? = values[key] as? Set<String> ?: defValues

    override fun getInt(
        key: String,
        defValue: Int
    ): Int = values[key] as? Int ?: defValue

    override fun getLong(
        key: String,
        defValue: Long
    ): Long = values[key] as? Long ?: defValue

    override fun getFloat(
        key: String,
        defValue: Float
    ): Float = values[key] as? Float ?: defValue

    override fun getBoolean(
        key: String,
        defValue: Boolean
    ): Boolean = values[key] as? Boolean ?: defValue

    override fun contains(key: String): Boolean = values.containsKey(key)

    override fun edit(): SharedPreferences.Editor =
        FakeSharedPreferencesEditor(
            currentKeys = { values.keys.toSet() },
            onApply = { changes: Map<String, Any?> -> applyChanges(changes = changes) }
        )

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        listeners.add(listener)
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        listeners.remove(listener)
    }

    private fun applyChanges(changes: Map<String, Any?>) {
        changes.forEach { (key: String, value: Any?) ->
            if (value == null) {
                values.remove(key)
            } else {
                values[key] = value
            }
        }
        changes.keys.forEach { key: String ->
            listeners.forEach { listener: SharedPreferences.OnSharedPreferenceChangeListener ->
                listener.onSharedPreferenceChanged(this, key)
            }
        }
    }
}