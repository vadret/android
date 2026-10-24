package fi.kroon.vadret.logging

import java.util.concurrent.ConcurrentHashMap

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

    fun getLogger(tag: String): Logger =
        DefaultLogger(
            tag = tag,
            sinks = sinks.values
        )
}