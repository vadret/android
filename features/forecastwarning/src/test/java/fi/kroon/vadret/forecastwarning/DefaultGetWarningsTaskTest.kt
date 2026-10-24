package fi.kroon.vadret.forecastwarning

import fi.kroon.vadret.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asLeft
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultGetWarningsTaskTest {

    private val warningFilterRepository = FakeWarningFilterRepository()
    private val aggregatedFeedRepository = FakeAggregatedFeedRepository()
    private val getWarningsTask: GetWarningsTask = DefaultGetWarningsTask(
        warningFilterRepository = warningFilterRepository,
        aggregatedFeedRepository = aggregatedFeedRepository
    )

    @Test
    fun `the feed is asked for the enabled sources and districts only`() = runTest {
        val result: Either<Failure, List<AggregatedFeed>> = getWarningsTask(isRefresh = true)

        assertEquals<Either<Failure, List<AggregatedFeed>>>(expected = aggregatedFeedRepository.result, actual = result)
        assertEquals(
            expected = listOf(Triple(setOf(FeedSource.SMHI), setOf(1), true)),
            actual = aggregatedFeedRepository.requests
        )
    }

    @Test
    fun `a filter that cannot be read fails without asking for the feed`() = runTest {
        val failure = Failure(errorCode = ErrorCode.DISTRICTS_NOT_AVAILABLE)
        warningFilterRepository.warningFilter = failure.asLeft()

        assertEquals(expected = Either.Left(a = failure), actual = getWarningsTask(isRefresh = false))
        assertTrue(actual = aggregatedFeedRepository.requests.isEmpty())
    }
}