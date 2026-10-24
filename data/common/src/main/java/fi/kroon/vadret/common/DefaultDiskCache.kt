package fi.kroon.vadret.common

import java.io.File
import java.io.IOException

private const val TEMPORARY_FILE_SUFFIX = ".tmp"

internal class DefaultDiskCache(
    private val directory: File
) : DiskCache {

    override fun read(key: String): String? {
        val file: File = file(key = key)
        return if (file.exists()) {
            file.readText()
        } else {
            null
        }
    }

    override fun write(
        key: String,
        value: String
    ) {
        directory.mkdirs()
        val temporaryFile: File = File.createTempFile(key, TEMPORARY_FILE_SUFFIX, directory)
        temporaryFile.writeText(value)
        if (!temporaryFile.renameTo(file(key = key))) {
            temporaryFile.delete()
            throw IOException("Could not write cache entry $key")
        }
    }

    override fun remove(key: String) {
        file(key = key).delete()
    }

    private fun file(key: String): File =
        File(directory, key)
}