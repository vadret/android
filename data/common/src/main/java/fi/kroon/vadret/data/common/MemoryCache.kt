package fi.kroon.vadret.data.common

import java.util.concurrent.ConcurrentHashMap

/**
 *  Keeps values for the life of the process. Every cache in
 *  the app holds a single entry, so nothing is evicted for size.
 */
class MemoryCache<K : Any, V : Any> {

    private val values: ConcurrentHashMap<K, V> = ConcurrentHashMap()

    fun get(key: K): V? = values[key]

    fun put(key: K, value: V) {
        values[key] = value
    }

    fun evictAll() {
        values.clear()
    }
}