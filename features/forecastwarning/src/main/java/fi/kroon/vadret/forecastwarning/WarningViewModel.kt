package fi.kroon.vadret.forecastwarning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.kroon.vadret.core.R
import fi.kroon.vadret.data.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.data.district.model.DistrictOptionEntity
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsource.model.FeedSourceOptionEntity
import fi.kroon.vadret.forecastwarning.districtpreference.UpdateDistrictPreferenceListTask
import fi.kroon.vadret.forecastwarning.feedsourcepreference.UpdateFeedSourcePreferenceListTask
import fi.kroon.vadret.forecastwarning.model.WarningFilter
import fi.kroon.vadret.forecastwarning.model.WarningItem
import fi.kroon.vadret.forecastwarning.warningfilter.GetWarningFilterOptionListService
import fi.kroon.vadret.shared.IViewModel
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.onResult
import io.github.sphrak.either.flatMap
import io.github.sphrak.either.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Clock

class WarningViewModel @Inject constructor(
    private val getWarningListService: GetWarningListService,
    private val getWarningFilterOptionListService: GetWarningFilterOptionListService,
    private val updateDistrictPreferenceListTask: UpdateDistrictPreferenceListTask,
    private val updateFeedSourcePreferenceListTask: UpdateFeedSourcePreferenceListTask,
    private val clock: Clock,
    private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel(), IViewModel {

    private val mutableState: MutableStateFlow<WarningState> = MutableStateFlow(WarningState())
    val state: StateFlow<WarningState> = mutableState.asStateFlow()

    private var loadJob: Job? = null

    init {
        load(forceNet = false)
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
            is WarningEvent.OnFeedSourceToggled -> onFeedSourceToggled(id = event.id)
            is WarningEvent.OnDistrictToggled -> onDistrictToggled(id = event.id)
        }
    }

    private fun onRefresh() {
        mutableState.update { state: WarningState ->
            state.copy(isRefreshing = true)
        }
        load(forceNet = true)
    }

    private fun load(forceNet: Boolean) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch(context = coroutineDispatcher) {
            getWarningListService(forceNet = forceNet)
                .map { aggregatedFeedList: List<AggregatedFeed> ->
                    WarningMapper(
                        entityList = aggregatedFeedList,
                        now = clock.now()
                    )
                }
                .onResult(
                    { failure: Failure ->
                        Timber.e("Failure: $failure")
                        mutableState.update { state: WarningState ->
                            state.copy(
                                isLoading = false,
                                isRefreshing = false,
                                errorMessageResourceId = getErrorCode(failure)
                            )
                        }
                    },
                    { warnings: List<WarningItem> ->
                        mutableState.update { state: WarningState ->
                            state.copy(
                                warnings = warnings,
                                isLoading = false,
                                isRefreshing = false,
                                isEmpty = warnings.isEmpty()
                            )
                        }
                    }
                )
        }
    }

    private fun onFilterButtonClicked() {
        viewModelScope.launch(context = coroutineDispatcher) {
            getWarningFilterOptionListService()
                .onResult(
                    { failure: Failure ->
                        Timber.e("Failure: $failure")
                        mutableState.update { state: WarningState ->
                            state.copy(errorMessageResourceId = getErrorCode(failure))
                        }
                    },
                    { data: GetWarningFilterOptionListService.Data ->
                        mutableState.update { state: WarningState ->
                            state.copy(
                                filter = WarningFilter(
                                    feedSourceOptions = data.feedSourceOptionList,
                                    districtOptions = data.districtOptionList
                                )
                            )
                        }
                    }
                )
        }
    }

    private fun onFeedSourceToggled(id: Int) {
        updateFilter { filter: WarningFilter ->
            filter.copy(
                feedSourceOptions = filter.feedSourceOptions.map { option: FeedSourceOptionEntity ->
                    if (option.id == id) {
                        option.copy(isEnabled = !option.isEnabled)
                    } else {
                        option
                    }
                },
                errorMessageResourceId = null
            )
        }
    }

    private fun onDistrictToggled(id: Int) {
        updateFilter { filter: WarningFilter ->
            filter.copy(
                districtOptions = filter.districtOptions.map { option: DistrictOptionEntity ->
                    if (option.id == id) {
                        option.copy(isEnabled = !option.isEnabled)
                    } else {
                        option
                    }
                },
                errorMessageResourceId = null
            )
        }
    }

    private fun updateFilter(transform: (WarningFilter) -> WarningFilter) {
        viewModelScope.launch(context = coroutineDispatcher) {
            mutableState.update { state: WarningState ->
                val filter: WarningFilter? = state.filter
                if (filter != null) {
                    state.copy(filter = transform(filter))
                } else {
                    state
                }
            }
        }
    }

    private fun onFilterApplied() {
        val filter: WarningFilter = mutableState.value.filter ?: return
        viewModelScope.launch(context = coroutineDispatcher) {
            val hasDistrict: Boolean = filter.districtOptions.any { option: DistrictOptionEntity -> option.isEnabled }
            val hasFeedSource: Boolean = filter.feedSourceOptions.any { option: FeedSourceOptionEntity -> option.isEnabled }
            if (!hasDistrict || !hasFeedSource) {
                mutableState.update { state: WarningState ->
                    state.copy(filter = filter.copy(errorMessageResourceId = R.string.filter_must_select_one))
                }
                return@launch
            }
            storeFilter(filter = filter)
                .onResult(
                    { failure: Failure ->
                        Timber.e("Failure: $failure")
                        mutableState.update { state: WarningState ->
                            state.copy(filter = filter.copy(errorMessageResourceId = getErrorCode(failure)))
                        }
                    },
                    {
                        mutableState.update { state: WarningState ->
                            state.copy(
                                filter = null,
                                isRefreshing = true
                            )
                        }
                        // the cached feed was fetched with the previous filter
                        load(forceNet = true)
                    }
                )
        }
    }

    private suspend fun storeFilter(filter: WarningFilter): Either<Failure, Int> =
        updateDistrictPreferenceListTask(entityList = filter.districtOptions)
            .flatMap {
                updateFeedSourcePreferenceListTask(entityList = filter.feedSourceOptions)
            }
}