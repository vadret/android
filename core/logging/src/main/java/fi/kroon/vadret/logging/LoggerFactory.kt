package fi.kroon.vadret.logging

import java.util.concurrent.ConcurrentHashMap

/**
 *  Holds the named sinks every logger writes to. A new destination
 *  is one [register] at startup, and loggers created before it
 *  start writing to it too.
 */
object LoggerFactory {

    private val sinks: ConcurrentHashMap<String, LogSink> = ConcurrentHashMap()

    fun register(
        name: String,
        sink: LogSink
    ) {
        sinks[name] = sink
    }

    fun unregister(name: String) {
        sinks.remove(name)
    }

    /**
     *  [tag] is the package of the module that logs.
     */
    fun getLogger(tag: String): Logger =
        DefaultLogger(
            tag = tag,
            sinks = sinks.values
        )
}