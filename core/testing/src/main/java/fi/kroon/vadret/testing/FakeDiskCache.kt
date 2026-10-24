package fi.kroon.vadret.testing

import fi.kroon.vadret.common.DiskCache
import java.util.concurrent.ConcurrentHashMap

class FakeDiskCache : DiskCache {

    val entries: ConcurrentHashMap<String, String> = ConcurrentHashMap()

    override fun read(key: String): String? = entries[key]

    override fun write(
        key: String,
        value: String
    ) {
        entries[key] = value
    }

    override fun remove(key: String) {
        entries.remove(key)
    }
}