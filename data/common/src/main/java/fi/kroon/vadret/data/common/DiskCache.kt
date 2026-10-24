package fi.kroon.vadret.data.common

import java.io.File

/**
 *  Stores one file per key under [directory]. Writes go
 *  through a temporary file so a crash never leaves a
 *  half written entry behind.
 */
class DiskCache(private val directory: File) {

    fun read(key: String): ByteArray? =
        file(key)
            .takeIf { it.exists() }
            ?.readBytes()

    fun write(key: String, bytes: ByteArray) {
        directory.mkdirs()
        val temporary = File(directory, "$key.tmp")
        temporary.writeBytes(bytes)
        if (!temporary.renameTo(file(key))) {
            temporary.delete()
            error("Could not write cache entry $key")
        }
    }

    fun remove(key: String): Boolean =
        file(key).delete()

    private fun file(key: String): File = File(directory, key)
}