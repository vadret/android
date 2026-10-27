package fi.kroon.vadret.testing

import fi.kroon.vadret.logging.LoggerFactory

@PublishedApi
internal const val RECORDING_SINK_NAME = "test"

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