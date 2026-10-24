package fi.kroon.vadret.forecastwarning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.kroon.vadret.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.core.R
import fi.kroon.vadret.forecastwarning.extension.toWarningFilterState
import fi.kroon.vadret.forecastwarning.extension.toWarningItem
import fi.kroon.vadret.forecastwarning.model.WarningFilterState
import fi.kroon.vadret.forecastwarning.model.WarningItem
import fi.kroon.vadret.ui.extension.messageResourceId
import fi.kroon.vadret.warningfilter.model.DistrictOption
import fi.kroon.vadret.warningfilter.model.FeedSourceOption
import fi.kroon.vadret.warningfilter.model.WarningFilter
import io.github.sphrak.either.extension.onResult
import io.github.sphrak.either.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Instant

/**
 *  Jobs are started and cancelled on the main thread only, the work
 *  itself runs on [coroutineDispatcher]. Edits to the filter run one
 *  at a time and in order, so applying always sees the last toggle.
 */
class WarningViewModel @Inject internal constructor(
    private val getWarningsTask: GetWarningsTask,
    private val getWarningFilterTask: GetWarningFilterTask,
    private val setWarningFilterTask: SetWarningFilterTask,
    private val clock: Clock,
    private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val mutableState: MutableStateFlow<WarningState> = MutableStateFlow(value = WarningState())
    val state: StateFlow<WarningState> = mutableState.asStateFlow()

    private val filterDispatcher: CoroutineDispatcher = coroutineDispatcher.limitedParallelism(parallelism = 1)

    private var loadJob: Job? = null

    init {
        load(isRefresh = false)
    }

    fun onEvent(event: WarningEvent) {
        when (event) {
            WarningEvent.OnRefresh -> onRefresh()
            WarningEvent.OnErrorShown -> mutableState.update { state: WarningState ->
                state.copy(errorMessageResourceId = null)
            }
            WarningEvent.OnFilterButtonClicked -> onFilterButtonClicked()
            WarningEvent.OnFilterDismissed -> mutableState.update { state: WarningState ->
                state.copy(filter = null)
            }
            WarningEvent.OnFilterApplied -> onFilterApplied()
            is WarningEvent.OnFeedSourceToggled -> updateFilter { filter: WarningFilterState ->
                filter.toggle(feedSource = event.feedSource)
            }
            is WarningEvent.OnDistrictToggled -> updateFilter { filter: WarningFilterState ->
                filter.toggle(districtId = event.districtId)
            }
        }
    }

    private fun onRefresh() {
        mutableState.update { state: WarningState ->
            state.copy(isRefreshing = true)
        }
        load(isRefresh = true)
    }

    private fun load(isRefresh: Boolean) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            withContext(context = coroutineDispatcher) {
                val now: Instant = clock.now()
                getWarningsTask(isRefresh = isRefresh)
                    .map { aggregatedFeedList: List<AggregatedFeed> ->
                        aggregatedFeedList.map { aggregatedFeed: AggregatedFeed -> aggregatedFeed.toWarningItem(now = now) }
                    }
                    .onResult(
                        onError = { failure: Failure -> displayError(failure = failure) },
                        onSuccess = { warnings: List<WarningItem> -> displayWarnings(warnings = warnings) }
                    )
            }
        }
    }

    private fun displayWarnings(warnings: List<WarningItem>) {
        mutableState.update { state: WarningState ->
            state.copy(
                warnings = warnings,
                isLoading = false,
                isRefreshing = false,
                isEmpty = warnings.isEmpty()
            )
        }
    }

    private fun displayError(failure: Failure) {
        mutableState.update { state: WarningState ->
            state.copy(
                isLoading = false,
                isRefreshing = false,
                errorMessageResourceId = failure.messageResourceId()
            )
        }
    }

    private fun onFilterButtonClicked() {
        viewModelScope.launch(context = coroutineDispatcher) {
            getWarningFilterTask()
                .onResult(
                    onError = { failure: Failure ->
                        mutableState.update { state: WarningState ->
                            state.copy(errorMessageResourceId = failure.messageResourceId())
                        }
                    },
                    onSuccess = { warningFilter: WarningFilter ->
                        mutableState.update { state: WarningState ->
                            state.copy(filter = warningFilter.toWarningFilterState())
                        }
                    }
                )
        }
    }

    private fun updateFilter(transform: (WarningFilterState) -> WarningFilterState) {
        viewModelScope.launch(context = filterDispatcher) {
            mutableState.update { state: WarningState ->
                val filter: WarningFilterState? = state.filter
                if (filter != null) {
                    state.copy(filter = transform(filter))
                } else {
                    state
                }
            }
        }
    }

    /**
     *  The stored feed was fetched for the old filter, so
     *  loading after a change fetches a new one.
     */
    private fun onFilterApplied() {
        viewModelScope.launch {
            val isApplied: Boolean = withContext(context = filterDispatcher) {
                applyFilter()
            }
            if (isApplied) {
                load(isRefresh = false)
            }
        }
    }

    /**
     *  Nothing is stored unless at least one district
     *  and one feed source stay selected.
     */
    private suspend fun applyFilter(): Boolean {
        val filter: WarningFilterState = mutableState.value.filter ?: return false
        val enabledDistrictIds: Set<Int> = filter.districts
            .filter { districtOption: DistrictOption -> districtOption.isEnabled }
            .map { districtOption: DistrictOption -> districtOption.districtId }
            .toSet()
        val enabledFeedSources: Set<FeedSource> = filter.feedSources
            .filter { feedSourceOption: FeedSourceOption -> feedSourceOption.isEnabled }
            .map { feedSourceOption: FeedSourceOption -> feedSourceOption.feedSource }
            .toSet()
        if (enabledDistrictIds.isEmpty() || enabledFeedSources.isEmpty()) {
            showFilterError(
                filter = filter,
                errorMessageResourceId = R.string.filter_must_select_one
            )
            return false
        }
        return setWarningFilterTask(
            enabledDistrictIds = enabledDistrictIds,
            enabledFeedSources = enabledFeedSources
        ).onResult(
            onError = { failure: Failure ->
                showFilterError(
                    filter = filter,
                    errorMessageResourceId = failure.messageResourceId()
                )
                false
            },
            onSuccess = {
                mutableState.update { state: WarningState ->
                    state.copy(
                        filter = null,
                        isRefreshing = true
                    )
                }
                true
            }
        )
    }

    private fun showFilterError(
        filter: WarningFilterState,
        errorMessageResourceId: Int
    ) {
        mutableState.update { state: WarningState ->
            state.copy(filter = filter.copy(errorMessageResourceId = errorMessageResourceId))
        }
    }

    private fun WarningFilterState.toggle(feedSource: FeedSource): WarningFilterState =
        copy(
            feedSources = feedSources.map { feedSourceOption: FeedSourceOption ->
                if (feedSourceOption.feedSource == feedSource) {
                    feedSourceOption.copy(isEnabled = !feedSourceOption.isEnabled)
                } else {
                    feedSourceOption
                }
            },
            errorMessageResourceId = null
        )

    private fun WarningFilterState.toggle(districtId: Int): WarningFilterState =
        copy(
            districts = districts.map { districtOption: DistrictOption ->
                if (districtOption.districtId == districtId) {
                    districtOption.copy(isEnabled = !districtOption.isEnabled)
                } else {
                    districtOption
                }
            },
            errorMessageResourceId = null
        )
}