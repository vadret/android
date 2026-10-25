package fi.kroon.vadret.common

import java.io.File

interface DiskCache {
    fun read(key: String): String?
    fun write(
        key: String,
        value: String
    )
    fun remove(key: String)
}

fun DiskCache(directory: File): DiskCache = DefaultDiskCache(directory = directory)