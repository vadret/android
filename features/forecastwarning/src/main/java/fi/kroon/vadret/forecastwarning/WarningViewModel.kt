package fi.kroon.vadret.forecastwarning

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
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
import fi.kroon.vadret.ui.extension.launchReplacing
import fi.kroon.vadret.ui.extension.messageResourceId
import fi.kroon.vadret.warningfilter.model.DistrictOption
import fi.kroon.vadret.warningfilter.model.FeedSourceOption
import fi.kroon.vadret.warningfilter.model.WarningFilter
import io.github.sphrak.either.extension.onResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.AtomicReference
import kotlin.time.Clock
import kotlin.time.Instant

@Immutable
class WarningViewModel internal constructor(
    private val getWarningsTask: GetWarningsTask,
    private val getWarningFilterTask: GetWarningFilterTask,
    private val setWarningFilterTask: SetWarningFilterTask,
    private val clock: Clock,
    private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    internal val state: StateFlow<WarningState>
        field = MutableStateFlow(value = WarningState())

    private val filterDispatcher: CoroutineDispatcher = coroutineDispatcher.limitedParallelism(parallelism = 1)
    private val isStarted: AtomicBoolean = AtomicBoolean(value = false)
    private val loadJob: AtomicReference<Job?> = AtomicReference(value = null)

    internal fun onEvent(event: WarningEvent) {
        when (event) {
            WarningEvent.OnStart -> onStart()

            WarningEvent.OnRefresh -> onRefresh()

            WarningEvent.OnErrorShown -> state.update { state: WarningState ->
                state.copy(errorMessageResourceId = null)
            }

            WarningEvent.OnFilterButtonClicked -> onFilterButtonClicked()

            WarningEvent.OnFilterDismissed -> state.update { state: WarningState ->
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

    private fun onStart() {
        if (isStarted.compareAndSet(expectedValue = false, newValue = true)) {
            load(isRefresh = false)
        }
    }

    private fun onRefresh() {
        state.update { state: WarningState ->
            state.copy(isRefreshing = true)
        }
        load(isRefresh = true)
    }

    private fun load(isRefresh: Boolean) {
        viewModelScope.launchReplacing(
            currentJob = loadJob,
            context = coroutineDispatcher
        ) {
            val now: Instant = clock.now()
            getWarningsTask(isRefresh = isRefresh)
                .onResult(
                    onError = { failure: Failure -> displayError(failure = failure) },
                    onSuccess = { aggregatedFeedList: List<AggregatedFeed> ->
                        displayWarnings(
                            warnings = aggregatedFeedList
                                .map { aggregatedFeed: AggregatedFeed -> aggregatedFeed.toWarningItem(now = now) }
                                .toImmutableList()
                        )
                    }
                )
        }
    }

    private fun displayWarnings(warnings: ImmutableList<WarningItem>) {
        state.update { state: WarningState ->
            state.copy(
                warnings = warnings,
                isLoading = false,
                isRefreshing = false,
                isEmpty = warnings.isEmpty()
            )
        }
    }

    private fun displayError(failure: Failure) {
        state.update { state: WarningState ->
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
                        state.update { state: WarningState ->
                            state.copy(errorMessageResourceId = failure.messageResourceId())
                        }
                    },
                    onSuccess = { warningFilter: WarningFilter ->
                        state.update { state: WarningState ->
                            state.copy(filter = warningFilter.toWarningFilterState())
                        }
                    }
                )
        }
    }

    private fun updateFilter(transform: (WarningFilterState) -> WarningFilterState) {
        viewModelScope.launch(context = filterDispatcher) {
            state.update { state: WarningState ->
                val filter: WarningFilterState? = state.filter
                if (filter != null) {
                    state.copy(filter = transform(filter))
                } else {
                    state
                }
            }
        }
    }

    private fun onFilterApplied() {
        viewModelScope.launch(context = filterDispatcher) {
            if (applyFilter()) {
                load(isRefresh = false)
            }
        }
    }

    private suspend fun applyFilter(): Boolean {
        val filter: WarningFilterState = state.value.filter ?: return false
        val enabledDistrictIds: Set<Int> = filter.districts
            .filter { districtOption: DistrictOption -> districtOption.isEnabled }
            .map { districtOption: DistrictOption -> districtOption.districtId }
            .toSet()
        val enabledFeedSources: Set<FeedSource> = filter.feedSources
            .filter { feedSourceOption: FeedSourceOption -> feedSourceOption.isEnabled }
            .map { feedSourceOption: FeedSourceOption -> feedSourceOption.feedSource }
            .toSet()
        if (enabledDistrictIds.isEmpty() || enabledFeedSources.isEmpty()) {
            showFilterError(errorMessageResourceId = R.string.filter_must_select_one)
            return false
        }
        return setWarningFilterTask(
            enabledDistrictIds = enabledDistrictIds,
            enabledFeedSources = enabledFeedSources
        ).onResult(
            onError = { failure: Failure ->
                showFilterError(errorMessageResourceId = failure.messageResourceId())
                false
            },
            onSuccess = {
                state.update { state: WarningState ->
                    state.copy(
                        filter = null,
                        isRefreshing = true
                    )
                }
                true
            }
        )
    }

    private fun showFilterError(@StringRes errorMessageResourceId: Int) {
        state.update { state: WarningState ->
            val filter: WarningFilterState? = state.filter
            if (filter != null) {
                state.copy(filter = filter.copy(errorMessageResourceId = errorMessageResourceId))
            } else {
                state
            }
        }
    }

    private fun WarningFilterState.toggle(feedSource: FeedSource): WarningFilterState =
        copy(
            feedSources = feedSources
                .map { feedSourceOption: FeedSourceOption ->
                    if (feedSourceOption.feedSource == feedSource) {
                        feedSourceOption.copy(isEnabled = !feedSourceOption.isEnabled)
                    } else {
                        feedSourceOption
                    }
                }
                .toImmutableList(),
            errorMessageResourceId = null
        )

    private fun WarningFilterState.toggle(districtId: Int): WarningFilterState =
        copy(
            districts = districts
                .map { districtOption: DistrictOption ->
                    if (districtOption.districtId == districtId) {
                        districtOption.copy(isEnabled = !districtOption.isEnabled)
                    } else {
                        districtOption
                    }
                }
                .toImmutableList(),
            errorMessageResourceId = null
        )
}