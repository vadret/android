package fi.kroon.vadret.aggregatedfeed

import androidx.sqlite.SQLiteException
import fi.kroon.vadret.aggregatedfeed.local.AggregatedFeedDatabase
import fi.kroon.vadret.aggregatedfeed.local.DefaultAggregatedFeedLocalDataSource
import fi.kroon.vadret.aggregatedfeed.local.DefaultSeenWarningLocalDataSource
import fi.kroon.vadret.aggregatedfeed.local.SeenWarningLocalDataSource
import fi.kroon.vadret.aggregatedfeed.local.inMemoryAggregatedFeedDatabase
import fi.kroon.vadret.aggregatedfeed.model.SeenWarning
import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.logging.LogLevel
import fi.kroon.vadret.testing.FakeDiskCache
import fi.kroon.vadret.testing.FixedClock
import fi.kroon.vadret.testing.LogLine
import fi.kroon.vadret.testing.withRecordedLogs
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asLeft
import io.github.sphrak.either.extension.asRight
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

private val now: Instant = Instant.parse(input = "2026-10-28T10:00:00Z")

class SeenWarningTest {

    private val testScope = TestScope(StandardTestDispatcher())
    private val clock = FixedClock(now = now)
    private val aggregatedFeedDatabase: AggregatedFeedDatabase = inMemoryAggregatedFeedDatabase(
        coroutineDispatcher = StandardTestDispatcher(scheduler = testScope.testScheduler)
    )
    private val feedRepository: FeedRepository = feedRepository(
        seenWarningLocalDataSource = DefaultSeenWarningLocalDataSource(aggregatedFeedDatabase = aggregatedFeedDatabase)
    )

    private fun feedRepository(seenWarningLocalDataSource: SeenWarningLocalDataSource): FeedRepository =
        DefaultFeedRepository(
            smhiWarningRemoteDataSource = FakeSmhiWarningRemoteDataSource(),
            krisinformationRemoteDataSource = FakeKrisinformationRemoteDataSource(),
            aggregatedFeedLocalDataSource = DefaultAggregatedFeedLocalDataSource(
                diskCache = FakeDiskCache(),
                coroutineDispatcher = StandardTestDispatcher(scheduler = testScope.testScheduler)
            ),
            seenWarningLocalDataSource = seenWarningLocalDataSource,
            clock = clock
        )

    @AfterTest
    fun tearDown() {
        aggregatedFeedDatabase.close()
    }

    @Test
    fun `a warning is marked seen at the current time`() =
        testScope.runTest {
            assertEquals(expected = Unit.asRight(), actual = feedRepository.markSeen(warningIdentifier = "smhi-1-2"))

            assertEquals(
                expected = listOf(
                    SeenWarning(
                        warningIdentifier = "smhi-1-2",
                        seenAt = now
                    )
                ).asRight(),
                actual = feedRepository.observeSeenWarnings().first()
            )
        }

    @Test
    fun `seeing a warning again moves its seen time forward`() =
        testScope.runTest {
            feedRepository.markSeen(warningIdentifier = "smhi-1-2")
            clock.now = now + 3.minutes
            feedRepository.markSeen(warningIdentifier = "smhi-1-2")

            assertEquals(
                expected = listOf(
                    SeenWarning(
                        warningIdentifier = "smhi-1-2",
                        seenAt = now + 3.minutes
                    )
                ).asRight(),
                actual = feedRepository.observeSeenWarnings().first()
            )
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `observing emits the seen warnings and then each change`() =
        testScope.runTest {
            val emitted = mutableListOf<Either<Failure, List<SeenWarning>>>()
            val job = launch {
                feedRepository.observeSeenWarnings().take(count = 3).toList(destination = emitted)
            }
            advanceUntilIdle()

            feedRepository.markSeen(warningIdentifier = "a")
            advanceUntilIdle()
            clock.now = now + 1.minutes
            feedRepository.markSeen(warningIdentifier = "b")
            advanceUntilIdle()
            job.join()

            assertEquals<List<Either<Failure, List<SeenWarning>>>>(
                expected = listOf(
                    emptyList<SeenWarning>().asRight(),
                    listOf(
                        SeenWarning(
                            warningIdentifier = "a",
                            seenAt = now
                        )
                    ).asRight(),
                    listOf(
                        SeenWarning(
                            warningIdentifier = "b",
                            seenAt = now + 1.minutes
                        ),
                        SeenWarning(
                            warningIdentifier = "a",
                            seenAt = now
                        )
                    ).asRight()
                ),
                actual = emitted
            )
        }

    @Test
    fun `storage that cannot be read or written is a storage failure`() =
        testScope.runTest {
            val failingFeedRepository: FeedRepository = feedRepository(
                seenWarningLocalDataSource = FailingSeenWarningLocalDataSource(exception = SQLiteException("disk full"))
            )

            withRecordedLogs { recordingLogSink ->
                val results: List<Either<Failure, Any>> = listOf(
                    failingFeedRepository.markSeen(warningIdentifier = "a"),
                    failingFeedRepository.observeSeenWarnings().toList().single()
                )

                assertEquals(
                    expected = List(size = results.size) { Failure(errorCode = ErrorCode.STORAGE_FAILED).asLeft() },
                    actual = results
                )
                assertEquals(
                    expected = List(size = results.size) { "fi.kroon.vadret.aggregatedfeed" to LogLevel.WARN },
                    actual = recordingLogSink.lines.map { line: LogLine -> line.tag to line.level }
                )
            }
        }

    @Test
    fun `anything else is unknown and logged as an error`() =
        testScope.runTest {
            val failingFeedRepository: FeedRepository = feedRepository(
                seenWarningLocalDataSource = FailingSeenWarningLocalDataSource(exception = IllegalStateException("bug"))
            )

            withRecordedLogs { recordingLogSink ->
                assertEquals(
                    expected = Failure.unknown().asLeft(),
                    actual = failingFeedRepository.markSeen(warningIdentifier = "a")
                )
                assertEquals(expected = LogLevel.ERROR, actual = recordingLogSink.lines.single().level)
            }
        }

    @Test
    fun `cancellation is rethrown`() =
        testScope.runTest {
            val failingFeedRepository: FeedRepository = feedRepository(
                seenWarningLocalDataSource = FailingSeenWarningLocalDataSource(exception = CancellationException("cancelled"))
            )

            assertFailsWith<CancellationException> {
                failingFeedRepository.markSeen(warningIdentifier = "a")
            }
        }
}