package fi.kroon.vadret.testing

import fi.kroon.vadret.logging.LoggerFactory

@PublishedApi
internal const val RECORDING_SINK_NAME = "test"

/**
 *  Registers a fresh [RecordingLogSink] for the duration of [block].
 */
inline fun <T> withRecordedLogs(block: (RecordingLogSink) -> T): T {
    val recordingLogSink = RecordingLogSink()
    LoggerFactory.register(
        name = RECORDING_SINK_NAME,
        sink = recordingLogSink
    )
    return try {
        block(recordingLogSink)
    } finally {
        LoggerFactory.unregister(name = RECORDING_SINK_NAME)
    }
}