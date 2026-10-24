package fi.kroon.vadret.testing

import android.content.SharedPreferences

/**
 *  Collects changes until [apply] or [commit] hands them to [onApply].
 *  A null value removes the key.
 */
internal class FakeSharedPreferencesEditor(
    private val currentKeys: () -> Set<String>,
    private val onApply: (Map<String, Any?>) -> Unit
) : SharedPreferences.Editor {

    private val changes: MutableMap<String, Any?> = mutableMapOf()

    override fun putString(
        key: String,
        value: String?
    ): SharedPreferences.Editor = put(
        key = key,
        value = value
    )

    override fun putStringSet(
        key: String,
        values: Set<String>?
    ): SharedPreferences.Editor = put(
        key = key,
        value = values
    )

    override fun putInt(
        key: String,
        value: Int
    ): SharedPreferences.Editor = put(
        key = key,
        value = value
    )

    override fun putLong(
        key: String,
        value: Long
    ): SharedPreferences.Editor = put(
        key = key,
        value = value
    )

    override fun putFloat(
        key: String,
        value: Float
    ): SharedPreferences.Editor = put(
        key = key,
        value = value
    )

    override fun putBoolean(
        key: String,
        value: Boolean
    ): SharedPreferences.Editor = put(
        key = key,
        value = value
    )

    override fun remove(key: String): SharedPreferences.Editor = put(
        key = key,
        value = null
    )

    override fun clear(): SharedPreferences.Editor {
        currentKeys().forEach { key: String -> changes[key] = null }
        return this
    }

    override fun commit(): Boolean {
        apply()
        return true
    }

    override fun apply() {
        onApply(changes.toMap())
        changes.clear()
    }

    private fun put(
        key: String,
        value: Any?
    ): SharedPreferences.Editor {
        changes[key] = value
        return this
    }
}