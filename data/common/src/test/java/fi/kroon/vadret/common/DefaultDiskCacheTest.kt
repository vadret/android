package fi.kroon.vadret.common

import java.io.File
import java.nio.file.Files
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DefaultDiskCacheTest {

    private val root: File = Files.createTempDirectory("disk-cache-test").toFile()
    private val directory = File(root, "cache")
    private val diskCache: DiskCache = DiskCache(directory = directory)

    @AfterTest
    fun tearDown() {
        root.deleteRecursively()
    }

    @Test
    fun `a missing entry reads as null`() {
        assertNull(actual = diskCache.read(key = "radar.json"))
    }

    @Test
    fun `a written entry reads back and creates the directory`() {
        diskCache.write(
            key = "radar.json",
            value = "{}"
        )

        assertEquals(expected = "{}", actual = diskCache.read(key = "radar.json"))
        assertTrue(actual = directory.isDirectory)
    }

    @Test
    fun `a second write replaces the first and leaves no temporary file`() {
        diskCache.write(
            key = "radar.json",
            value = "first"
        )
        diskCache.write(
            key = "radar.json",
            value = "second"
        )

        assertEquals(expected = "second", actual = diskCache.read(key = "radar.json"))
        assertEquals(expected = listOf("radar.json"), actual = directory.list()?.toList())
    }

    @Test
    fun `a removed entry reads as null`() {
        diskCache.write(
            key = "radar.json",
            value = "{}"
        )

        diskCache.remove(key = "radar.json")

        assertNull(actual = diskCache.read(key = "radar.json"))
    }

    @Test
    fun `removing a missing entry does nothing`() {
        diskCache.remove(key = "radar.json")

        assertNull(actual = diskCache.read(key = "radar.json"))
    }
}