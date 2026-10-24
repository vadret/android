package fi.kroon.vadret.logging

/**
 *  Ordered from least to most severe, so a sink can
 *  accept everything from a level and up.
 */
enum class LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR
}