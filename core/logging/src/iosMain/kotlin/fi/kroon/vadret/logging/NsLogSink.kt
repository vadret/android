package fi.kroon.vadret.logging

import platform.Foundation.NSLog

private const val LINE_FORMAT = "%@ %@ %@"

fun nsLogSink(minimumLevel: LogLevel): LogSink = DefaultNsLogSink(minimumLevel = minimumLevel)

internal class DefaultNsLogSink(
    private val minimumLevel: LogLevel
) : LogSink {

    override fun accepts(level: LogLevel): Boolean = level >= minimumLevel

    override fun write(
        level: LogLevel,
        tag: String,
        message: String
    ) {
        NSLog(LINE_FORMAT, level.name, tag, message)
    }
}