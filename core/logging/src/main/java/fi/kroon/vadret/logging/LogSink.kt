package fi.kroon.vadret.logging

/**
 *  A destination for log lines. The binding to a logging
 *  framework lives in a sink and nowhere else.
 */
interface LogSink {
    fun accepts(level: LogLevel): Boolean
    fun write(
        level: LogLevel,
        tag: String,
        message: String
    )
}