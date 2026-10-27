package fi.kroon.vadret.testing

import fi.kroon.vadret.logging.LogLevel

data class LogLine(
    val level: LogLevel,
    val tag: String,
    val message: String
)