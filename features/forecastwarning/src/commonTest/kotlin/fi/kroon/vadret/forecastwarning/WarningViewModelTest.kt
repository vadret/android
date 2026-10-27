package fi.kroon.vadret.forecastwarning

import fi.kroon.vadret.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.core.resources.Res
import fi.kroon.vadret.core.resources.filter_failure_districts_not_available
import fi.kroon.vadret.core.resources.filter_must_select_one
import fi.kroon.vadret.core.resources.no_network_available
import fi.kroon.vadret.core.resources.unhandled_error
import fi.kroon.vadret.forecastwarning.model.WarningFilterState
import fi.kroon.vadret.forecastwarning.model.WarningItem
import fi.kroon.vadret.testing.FixedClock
import fi.kroon.vadret.warning.GetWarningsTask
import io.github.sphrak.either.extension.asLeft
import io.github.sphrak.either.extension.asRight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class WarningViewModelTest {

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val warningFilterRepository = FakeWarningFilterRepository()
    private val aggregatedFeedRepository = FakeAggregatedFeedRepository()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher = testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(): WarningViewModel {
        val warningViewModel = WarningViewModel(
            getWarningsTask = GetWarningsTask(
                warningFilterRepository = warningFilterRepository,
                aggregatedFeedRepository = aggregatedFeedRepository
            ),
            getWarningFilterTask = DefaultGetWarningFilterTask(warningFilterRepository = warningFilterRepository),
            setWarningFilterTask = DefaultSetWarningFilterTask(warningFilterRepository = warningFilterRepository),
            clock = FixedClock(now = Instant.parse(input = "2026-10-24T08:30:00Z")),
            coroutineDispatcher = testDispatcher
        )
        warningViewModel.onEvent(event = WarningEvent.OnStart)
        return warningViewModel
    }

    @Test
    fun `nothing loads until the screen starts the view model and then only once`() =
        testScope.runTest {
            val warningViewModel = WarningViewModel(
                getWarningsTask = GetWarningsTask(
                    warningFilterRepository = warningFilterRepository,
                    aggregatedFeedRepository = aggregatedFeedRepository
                ),
                getWarningFilterTask = DefaultGetWarningFilterTask(warningFilterRepository = warningFilterRepository),
                setWarningFilterTask = DefaultSetWarningFilterTask(warningFilterRepository = warningFilterRepository),
                clock = FixedClock(now = Instant.parse(input = "2026-10-24T08:30:00Z")),
                coroutineDispatcher = testDispatcher
            )
            advanceUntilIdle()
            val requestsBeforeStart: Int = aggregatedFeedRepository.requests.size

            warningViewModel.onEvent(event = WarningEvent.OnStart)
            warningViewModel.onEvent(event = WarningEvent.OnStart)
            advanceUntilIdle()

            assertEquals(expected = 0, actual = requestsBeforeStart)
            assertEquals(expected = 1, actual = aggregatedFeedRepository.requests.size)
        }

    @Test
    fun `warnings are shown once loaded`() =
        testScope.runTest {
            val warningViewModel: WarningViewModel = viewModel()
            assertTrue(actual = warningViewModel.state.value.isLoading)

            advanceUntilIdle()

            val state: WarningState = warningViewModel.state.value
            assertEquals(
                expected = listOf("smhi-1-1"),
                actual = state.warnings.map { warningItem: WarningItem -> warningItem.identifier }
            )
            assertEquals(expected = false, actual = state.isLoading)
            assertEquals(expected = false, actual = state.isEmpty)
            assertEquals(expected = false, actual = aggregatedFeedRepository.requests.single().third)
        }

    @Test
    fun `no warnings is told apart from a failed load`() =
        testScope.runTest {
            aggregatedFeedRepository.result = emptyList<AggregatedFeed>().asRight()
            val emptyViewModel: WarningViewModel = viewModel()
            advanceUntilIdle()
            aggregatedFeedRepository.result = Failure(errorCode = ErrorCode.NETWORK_UNAVAILABLE).asLeft()
            val failedViewModel: WarningViewModel = viewModel()
            advanceUntilIdle()

            assertTrue(actual = emptyViewModel.state.value.isEmpty)
            assertEquals(expected = false, actual = failedViewModel.state.value.isEmpty)
            assertEquals(
                expected = Res.string.no_network_available,
                actual = failedViewModel.state.value.errorMessageResource
            )
            failedViewModel.onEvent(event = WarningEvent.OnErrorShown)
            assertNull(actual = failedViewModel.state.value.errorMessageResource)
        }

    @Test
    fun `a refresh skips the cache`() =
        testScope.runTest {
            val warningViewModel: WarningViewModel = viewModel()
            advanceUntilIdle()

            warningViewModel.onEvent(event = WarningEvent.OnRefresh)
            assertTrue(actual = warningViewModel.state.value.isRefreshing)
            advanceUntilIdle()

            assertEquals(
                expected = listOf(false, true),
                actual = aggregatedFeedRepository.requests.map { request -> request.third }
            )
            assertEquals(expected = false, actual = warningViewModel.state.value.isRefreshing)
        }

    @Test
    fun `the filter opens with the stored selection and toggles until applied`() =
        testScope.runTest {
            val warningViewModel: WarningViewModel = viewModel()
            advanceUntilIdle()

            warningViewModel.onEvent(event = WarningEvent.OnFilterButtonClicked)
            advanceUntilIdle()
            warningViewModel.onEvent(event = WarningEvent.OnDistrictToggled(districtId = gotland.districtId))
            warningViewModel.onEvent(event = WarningEvent.OnFeedSourceToggled(feedSource = FeedSource.KRISINFORMATION))
            advanceUntilIdle()

            val filter: WarningFilterState? = warningViewModel.state.value.filter
            assertEquals(
                expected = listOf(true, true),
                actual = filter?.districts?.map { districtOption -> districtOption.isEnabled }
            )
            assertEquals(
                expected = listOf(true, true),
                actual = filter?.feedSources?.map { feedSourceOption -> feedSourceOption.isEnabled }
            )
            assertTrue(actual = warningFilterRepository.storedFilters.isEmpty())
        }

    @Test
    fun `applying stores the filter and loads the feed for it`() =
        testScope.runTest {
            val warningViewModel: WarningViewModel = viewModel()
            advanceUntilIdle()
            warningViewModel.onEvent(event = WarningEvent.OnFilterButtonClicked)
            advanceUntilIdle()

            warningViewModel.onEvent(event = WarningEvent.OnDistrictToggled(districtId = gotland.districtId))
            warningViewModel.onEvent(event = WarningEvent.OnFilterApplied)
            advanceUntilIdle()

            assertEquals(
                expected = listOf(setOf(gotland.districtId, stockholm.districtId) to setOf(FeedSource.SMHI)),
                actual = warningFilterRepository.storedFilters
            )
            assertNull(actual = warningViewModel.state.value.filter)
            assertEquals(expected = 2, actual = aggregatedFeedRepository.requests.size)
        }

    @Test
    fun `a filter without a district or a feed source is not stored`() =
        testScope.runTest {
            val warningViewModel: WarningViewModel = viewModel()
            advanceUntilIdle()
            warningViewModel.onEvent(event = WarningEvent.OnFilterButtonClicked)
            advanceUntilIdle()

            warningViewModel.onEvent(event = WarningEvent.OnFeedSourceToggled(feedSource = FeedSource.SMHI))
            warningViewModel.onEvent(event = WarningEvent.OnFilterApplied)
            advanceUntilIdle()

            assertEquals(
                expected = Res.string.filter_must_select_one,
                actual = warningViewModel.state.value.filter?.errorMessageResource
            )
            assertTrue(actual = warningFilterRepository.storedFilters.isEmpty())

            warningViewModel.onEvent(event = WarningEvent.OnFeedSourceToggled(feedSource = FeedSource.SMHI))
            advanceUntilIdle()
            assertNull(actual = warningViewModel.state.value.filter?.errorMessageResource)
        }

    @Test
    fun `a filter that cannot be stored stays open with the error`() =
        testScope.runTest {
            warningFilterRepository.setResult = Failure(errorCode = ErrorCode.STORAGE_FAILED).asLeft()
            val warningViewModel: WarningViewModel = viewModel()
            advanceUntilIdle()
            warningViewModel.onEvent(event = WarningEvent.OnFilterButtonClicked)
            advanceUntilIdle()

            warningViewModel.onEvent(event = WarningEvent.OnFilterApplied)
            advanceUntilIdle()

            assertEquals(
                expected = Res.string.unhandled_error,
                actual = warningViewModel.state.value.filter?.errorMessageResource
            )
            assertEquals(expected = 1, actual = aggregatedFeedRepository.requests.size)
        }

    @Test
    fun `dismissing the filter forgets the edits`() =
        testScope.runTest {
            val warningViewModel: WarningViewModel = viewModel()
            advanceUntilIdle()
            warningViewModel.onEvent(event = WarningEvent.OnFilterButtonClicked)
            advanceUntilIdle()

            warningViewModel.onEvent(event = WarningEvent.OnFilterDismissed)

            assertNull(actual = warningViewModel.state.value.filter)
        }

    @Test
    fun `a filter that cannot be read stays closed and shows the error`() =
        testScope.runTest {
            val warningViewModel: WarningViewModel = viewModel()
            advanceUntilIdle()
            warningFilterRepository.warningFilter = Failure(errorCode = ErrorCode.DISTRICTS_NOT_AVAILABLE).asLeft()

            warningViewModel.onEvent(event = WarningEvent.OnFilterButtonClicked)
            advanceUntilIdle()

            assertEquals(
                expected = Res.string.filter_failure_districts_not_available,
                actual = warningViewModel.state.value.errorMessageResource
            )
            assertNull(actual = warningViewModel.state.value.filter)
        }

    @Test
    fun `toggling or applying without an open filter changes nothing`() =
        testScope.runTest {
            val warningViewModel: WarningViewModel = viewModel()
            advanceUntilIdle()
            val stateBefore: WarningState = warningViewModel.state.value

            warningViewModel.onEvent(event = WarningEvent.OnDistrictToggled(districtId = gotland.districtId))
            warningViewModel.onEvent(event = WarningEvent.OnFeedSourceToggled(feedSource = FeedSource.KRISINFORMATION))
            warningViewModel.onEvent(event = WarningEvent.OnFilterApplied)
            advanceUntilIdle()

            assertEquals(expected = stateBefore, actual = warningViewModel.state.value)
            assertTrue(actual = warningFilterRepository.storedFilters.isEmpty())
            assertEquals(expected = 1, actual = aggregatedFeedRepository.requests.size)
        }
}