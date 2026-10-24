package fi.kroon.vadret.logging

/**
 *  Messages are lambdas so they are only built
 *  when some sink wants the level.
 */
interface Logger {
    fun debug(message: () -> String)
    fun info(message: () -> String)
    fun warn(message: () -> String)
    fun error(message: () -> String)
}