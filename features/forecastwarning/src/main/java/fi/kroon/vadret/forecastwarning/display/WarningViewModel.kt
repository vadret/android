package fi.kroon.vadret.forecastwarning.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.kroon.vadret.data.aggregatedfeed.exception.AggregatedFeedFailure
import fi.kroon.vadret.data.district.exception.DistrictFailure
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.forecastwarning.CountDistrictPreferenceEntityTask
import fi.kroon.vadret.forecastwarning.CountFeedSourcePreferenceEntityTask
import fi.kroon.vadret.forecastwarning.SetDefaultDistrictPreferenceEntityService
import fi.kroon.vadret.forecastwarning.SetDefaultFeedSourcePreferenceEntityService
import fi.kroon.vadret.forecastwarning.aggregatedfeed.GetAggregatedFeedLastCheckedTask
import fi.kroon.vadret.forecastwarning.aggregatedfeed.GetAggregatedFeedService
import fi.kroon.vadret.forecastwarning.aggregatedfeed.SetAggregatedFeedLastCheckedTask
import fi.kroon.vadret.forecastwarning.display.WarningFragment.Companion.RESULT_OK
import fi.kroon.vadret.forecastwarning.display.model.IWarningModel
import fi.kroon.vadret.forecastwarning.district.GetDistrictNetService
import fi.kroon.vadret.forecastwarning.districtpreference.GetEnabledDistrictPreferenceIdsTask
import fi.kroon.vadret.forecastwarning.feedsource.GetFeedSourceNetService
import fi.kroon.vadret.forecastwarning.feedsourcepreference.GetEnabledFeedSourcePreferenceIdsTask
import fi.kroon.vadret.shared.IViewModel
import io.github.sphrak.either.extension.onResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
class WarningViewModel @Inject constructor(
    private var stateModel: WarningView.State,
    private val state: MutableSharedFlow<WarningView.State>,
    private val getDistrictNetService: GetDistrictNetService,
    private val getFeedSourceNetService: GetFeedSourceNetService,
    private val getAggregatedFeedService: GetAggregatedFeedService,
    private val getEnabledDistrictPreferenceIdsTask: GetEnabledDistrictPreferenceIdsTask,
    private val getEnabledFeedSourcePreferenceIdsTask: GetEnabledFeedSourcePreferenceIdsTask,
    private val getAggregatedFeedLastCheckedTask: GetAggregatedFeedLastCheckedTask,
    private val setAggregatedFeedLastCheckedTask: SetAggregatedFeedLastCheckedTask,
    private val countDistrictPreferenceEntityTask: CountDistrictPreferenceEntityTask,
    private val countFeedSourcePreferenceEntityTask: CountFeedSourcePreferenceEntityTask,
    private val setDefaultDistrictPreferenceEntityService: SetDefaultDistrictPreferenceEntityService,
    private val setDefaultFeedSourcePreferenceEntityService: SetDefaultFeedSourcePreferenceEntityService
) : ViewModel(), IViewModel {

    val viewState: SharedFlow<WarningView.State> get() = state.asSharedFlow()

    fun send(event: WarningView.Event) {
        viewModelScope.launch { reduce(event = event) }
    }

    private suspend fun reduce(event: WarningView.Event): Unit =
        when (event) {
            is WarningView.Event.OnViewInitialised -> onViewInitialisedEvent(event.stateParcel)
            WarningView.Event.OnFailureHandled -> onFailureHandledEvent()
            WarningView.Event.OnProgressBarEffectStarted -> onProgressBarEffectStartedEvent()
            WarningView.Event.OnProgressBarEffectStopped -> onProgressBarEffectStoppedEvent()
            WarningView.Event.OnSwipedToRefresh -> onSwipedToRefreshEvent()
            WarningView.Event.OnWarningListDisplayed -> onAlertListDisplayedEvent()
            WarningView.Event.OnScrollPositionRestored -> onScrollPositionRestoredEvent()
            WarningView.Event.OnStateParcelUpdated -> onStateParcelUpdatedEvent()
            WarningView.Event.OnFilterButtonToggled -> onFilterButtonToggled()
            WarningView.Event.OnNoWarningsIssuedDisplayed -> onNoWarningsIssuedDisplayed()
            is WarningView.Event.OnWarningFilterResult -> onWarningFilterResult(event.result)
        }

    private suspend fun onWarningFilterResult(result: String) {
        if (result == RESULT_OK) {
            // the cached feed was fetched with the previous filter
            stateModel = stateModel.copy(renderEvent = WarningView.RenderEvent.None, forceNet = true)
            onViewInitialisedEvent(null)
        } else {
            stateModel = stateModel.copy(renderEvent = WarningView.RenderEvent.None)
            state.emit(stateModel)
        }
    }

    private suspend fun onNoWarningsIssuedDisplayed() =
        endLoadingWarning()

    private suspend fun preLoadingWarning() {
        when {
            stateModel.startRefreshing -> {
                stateModel = stateModel.copy(
                    renderEvent = WarningView.RenderEvent.StartProgressBarEffect,
                    startRefreshing = false,
                    stopRefreshing = true
                )
                state.emit(stateModel)
            }
            else -> {
                Timber.d("PRE LOADING AGGREGATED FEED END")
                stateModel = stateModel.copy(
                    isInitialised = true
                )
                loadDistrict()
            }
        }
    }

    private suspend fun onFilterButtonToggled() {
        val renderEvent: WarningView.RenderEvent.NavigateToFilter =
            WarningView.RenderEvent.NavigateToFilter
        stateModel = stateModel.copy(renderEvent = renderEvent)
        state.emit(stateModel)
    }

    private suspend fun setDefaultFeedSourcePreference() = withContext(Dispatchers.IO) {
        countFeedSourcePreferenceEntityTask()
            .await()
            .onResult(
                { failure: Failure ->
                    displayError(failure)
                },
                { feedSourceListAvailable: Boolean ->
                    Timber.d("FEED SOURCE PREFS ARE AVAILABLE: $feedSourceListAvailable")
                    when (feedSourceListAvailable) {
                        /**
                         *  [loadLastChecked] starts loading preferences
                         *  needed to complete aggregated feed request.
                         */
                        /**
                         *  [loadLastChecked] starts loading preferences
                         *  needed to complete aggregated feed request.
                         */
                        true -> loadLastChecked()
                        false ->
                            setDefaultFeedSourcePreferenceEntityService()
                                .await()
                                .onResult(
                                    { failure: Failure ->
                                        displayError(failure)
                                    },
                                    { feedSourceIsAvailable: Boolean ->
                                        when (feedSourceIsAvailable) {
                                            true -> loadLastChecked()
                                            false -> {
                                                Timber.e("This is a deeply erronous state, this should really not happen")
                                                displayError(AggregatedFeedFailure.LoadingPreferenceFailed)
                                            }
                                        }
                                    }
                                )
                    }
                }
            )
    }

    private suspend fun setDefaultDistrictPreference() = withContext(Dispatchers.IO) {
        countDistrictPreferenceEntityTask()
            .await()
            .onResult(
                { failure: Failure ->
                    displayError(failure)
                },
                { districtPreferenceAvailable: Boolean ->
                    Timber.d("DISTRICT PREFS ARE AVAILABLE: $districtPreferenceAvailable")
                    when (districtPreferenceAvailable) {
                        true -> setDefaultFeedSourcePreference()
                        false ->
                            setDefaultDistrictPreferenceEntityService()
                                .await()
                                .onResult(
                                    { failure: Failure ->
                                        displayError(failure)
                                    },
                                    { districtPreferencesAvailable: Boolean ->
                                        when (districtPreferencesAvailable) {
                                            true -> setDefaultFeedSourcePreference()
                                            false -> {
                                                Timber.e("This is a deeply erronous state, this should really not happen")
                                                displayError(AggregatedFeedFailure.LoadingPreferenceFailed)
                                            }
                                        }
                                    }
                                )
                    }
                }
            )
    }

    private suspend fun endLoadingWarning() {
        when {
            stateModel.wasRestoredFromStateParcel -> {
                stateModel = stateModel.copy(
                    renderEvent = WarningView.RenderEvent.RestoreScrollPosition,
                    wasRestoredFromStateParcel = false
                )
                state.emit(stateModel)
            }
            stateModel.stopRefreshing -> {
                stateModel = stateModel.copy(
                    renderEvent = WarningView.RenderEvent.StopProgressBarEffect,
                    stopRefreshing = false
                )
                state.emit(stateModel)
            }
            else -> {
                Timber.d("endLoadingWarning ended.")
                updateStateParcel()
            }
        }
    }

    private fun restoreStateFromStateParcel(stateParcel: WarningView.StateParcel?) {
        Timber.d("restoreStateFromStateParcel: $stateParcel")
        stateParcel?.run {
            stateModel = stateModel.copy(
                forceNet = forceNet,
                wasRestoredFromStateParcel = true,
                startRefreshing = startRefreshing,
                stopRefreshing = stopRefreshing
            )
        }
    }

    private suspend fun updateStateParcel() {
        Timber.d("updateStateParcel")
        stateModel = stateModel.copy(
            renderEvent = WarningView.RenderEvent.UpdateStateParcel,
            startRefreshing = false,
            stopRefreshing = false,
            forceNet = false
        )
        state.emit(stateModel)
    }

    private suspend fun updateStateToLoadingAndRefreshing() {
        Timber.d("updateStateToLoadingAndRefreshing")
        stateModel = stateModel.copy(
            startRefreshing = stateModel.wasRestoredFromStateParcel.not()
        )
        state.emit(stateModel)
    }

    private suspend fun updateStateToRefreshing() {
        Timber.d("updateStateToRefreshing")
        stateModel = stateModel.copy(
            startRefreshing = true
        )
        state.emit(stateModel)
    }

    private suspend fun onViewInitialisedEvent(stateParcel: WarningView.StateParcel?) = withContext(Dispatchers.IO) {
        restoreStateFromStateParcel(stateParcel)
        updateStateToLoadingAndRefreshing()
        preLoadingWarning()
    }

    private suspend fun loadLastChecked() = withContext(Dispatchers.IO) {
        getAggregatedFeedLastCheckedTask()
            .await()
            .onResult(
                { failure: Failure ->
                    displayError(failure)
                },
                { timeStamp: Long ->
                    loadDistrictIds(timeStamp = timeStamp)
                }
            )
    }

    private suspend fun loadDistrictIds(timeStamp: Long) = withContext(Dispatchers.IO) {
        getEnabledDistrictPreferenceIdsTask()
            .await()
            .onResult(
                { failure: Failure ->
                    displayError(failure)
                },
                { districtIds: List<Int> ->
                    loadFeedSourceIds(
                        districtIds = districtIds,
                        timeStamp = timeStamp
                    )
                }
            )
    }

    private suspend fun loadFeedSourceIds(
        timeStamp: Long,
        districtIds: List<Int>
    ) = withContext(Dispatchers.IO) {
        getEnabledFeedSourcePreferenceIdsTask()
            .await()
            .onResult(
                { failure: Failure ->
                    displayError(failure)
                },
                { feedSourceIds: List<Int> ->
                    loadAggregatedFeed(
                        timeStamp = timeStamp,
                        districtIds = districtIds,
                        feedSourceIds = feedSourceIds
                    )
                }
            )
    }

    private suspend fun loadAggregatedFeed(
        timeStamp: Long,
        districtIds: List<Int>,
        feedSourceIds: List<Int>
    ) = withContext(Dispatchers.IO) {
        getAggregatedFeedService(
            timeStamp = timeStamp,
            forceNet = stateModel.forceNet,
            districtIds = districtIds,
            feedSourceIds = feedSourceIds
        ).await()
            .onResult(
                { failure: Failure ->
                    displayError(failure)
                },
                { data: GetAggregatedFeedService.Data ->

                    Timber.d("WARNING LIST RAW: ${data.aggregatedFeedList}")
                    val warningModelList: MutableList<IWarningModel> =
                        WarningMapper(entityList = data.aggregatedFeedList)
                    Timber.d("WARNING LIST: $warningModelList")

                    val renderEvent: WarningView.RenderEvent =
                        if (data.aggregatedFeedList.isNotEmpty()) {
                            WarningView.RenderEvent.DisplayAggregatedFeed(list = warningModelList)
                        } else {
                            WarningView.RenderEvent.DisplayNoWarningsIssued
                        }
                    stateModel = stateModel.copy(renderEvent = renderEvent)

                    setAggregatedFeedLastCheckedTask(data.timeStamp)
                        .await()
                        .onResult(
                            { failure: Failure ->
                                Timber.e("failure: $failure")
                                state.emit(stateModel)
                            },
                            {
                                state.emit(stateModel)
                            }
                        )
                }
            )
    }

    private suspend fun loadDistrict() = withContext(Dispatchers.IO) {
        getDistrictNetService()
            .await()
            .onResult(
                { failure: Failure ->
                    displayError(failure)
                },
                { districtIsAvailable: Boolean ->
                    when (districtIsAvailable) {
                        true -> loadFeedSource()
                        false -> {
                            val errorCode: Int =
                                getErrorCode(DistrictFailure.DistrictNotAvailable)
                            val renderEvent: WarningView.RenderEvent.DisplayError =
                                WarningView.RenderEvent.DisplayError(errorCode)
                            stateModel = stateModel.copy(renderEvent = renderEvent)
                            state.emit(stateModel)
                        }
                    }
                }
            )
    }

    private suspend fun loadFeedSource() = withContext(Dispatchers.IO) {
        getFeedSourceNetService()
            .await()
            .onResult(
                { failure: Failure ->
                    displayError(failure)
                },
                { feedSourceIsAvailable: Boolean ->
                    when (feedSourceIsAvailable) {
                        true -> setDefaultDistrictPreference()
                        false -> {
                            val errorCode: Int =
                                getErrorCode(DistrictFailure.DistrictNotAvailable)
                            val renderEvent: WarningView.RenderEvent.DisplayError =
                                WarningView.RenderEvent.DisplayError(errorCode)
                            stateModel = stateModel.copy(renderEvent = renderEvent)
                            state.emit(stateModel)
                        }
                    }
                }
            )
    }

    /**
     *  Emitting [stateModel] unchanged on failure would replay
     *  its last render event, and when that is
     *  [WarningView.RenderEvent.StartProgressBarEffect] loading
     *  restarts in a tight loop.
     */
    private suspend fun displayError(failure: Failure) {
        Timber.e("Failure: $failure")
        val errorCode: Int = getErrorCode(failure)
        stateModel = stateModel.copy(renderEvent = WarningView.RenderEvent.DisplayError(errorCode))
        state.emit(stateModel)
    }

    private suspend fun onFailureHandledEvent() = endLoadingWarning()

    private suspend fun onProgressBarEffectStartedEvent() {
        preLoadingWarning()
    }

    private suspend fun onProgressBarEffectStoppedEvent() {
        endLoadingWarning()
    }

    private suspend fun onSwipedToRefreshEvent() {
        Timber.d("SWIPED ALERT REFRESH")
        stateModel = stateModel.copy(forceNet = true)
        updateStateToRefreshing()
        preLoadingWarning()
    }

    private suspend fun onAlertListDisplayedEvent() {
        endLoadingWarning()
    }

    private suspend fun onScrollPositionRestoredEvent() {
        endLoadingWarning()
    }

    private suspend fun onStateParcelUpdatedEvent() {
        stateModel = stateModel.copy(
            renderEvent = WarningView.RenderEvent.None
        )
        state.emit(stateModel)
    }
}