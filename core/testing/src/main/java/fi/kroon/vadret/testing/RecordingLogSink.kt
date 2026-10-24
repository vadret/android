package fi.kroon.vadret.testing

import fi.kroon.vadret.logging.LogLevel
import fi.kroon.vadret.logging.LogSink
import java.util.concurrent.CopyOnWriteArrayList

/**
 *  Keeps every line so tests can assert what was logged.
 */
class RecordingLogSink : LogSink {

    private val recordedLines: CopyOnWriteArrayList<LogLine> = CopyOnWriteArrayList()

    val lines: List<LogLine>
        get() = recordedLines.toList()

    override fun accepts(level: LogLevel): Boolean = true

    override fun write(
        level: LogLevel,
        tag: String,
        message: String
    ) {
        recordedLines.add(
            LogLine(
                level = level,
                tag = tag,
                message = message
            )
        )
    }
}