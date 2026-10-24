package fi.kroon.vadret.common

import java.io.File

/**
 *  One text file per key. Reads and writes block, so
 *  callers run them off the main thread.
 */
interface DiskCache {
    fun read(key: String): String?
    fun write(
        key: String,
        value: String
    )
    fun remove(key: String)
}

fun DiskCache(directory: File): DiskCache =
    DefaultDiskCache(directory = directory)