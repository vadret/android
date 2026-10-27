package fi.kroon.vadret.logging

import kotlin.test.Test
import kotlin.test.assertEquals

class NsLogSinkTest {

    @Test
    fun `sink accepts its minimum level and up`() {
        val sink: LogSink = nsLogSink(minimumLevel = LogLevel.WARN)

        assertEquals(
            expected = listOf(false, false, true, true),
            actual = LogLevel.entries.map { level: LogLevel -> sink.accepts(level = level) }
        )
    }
}