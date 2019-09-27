package fi.kroon.vadret.presentation.warning.display

import fi.kroon.vadret.data.district.exception.DistrictFailure
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.domain.aggregatedfeed.GetAggregatedFeedLastCheckedTask
import fi.kroon.vadret.domain.aggregatedfeed.GetAggregatedFeedService
import fi.kroon.vadret.domain.aggregatedfeed.SetAggregatedFeedLastCheckedTask
import fi.kroon.vadret.domain.district.GetDistrictNetService
import fi.kroon.vadret.domain.districtpreference.GetEnabledDistrictPreferenceIdsTask
import fi.kroon.vadret.domain.feedsource.GetFeedSourceNetService
import fi.kroon.vadret.domain.feedsourcepreference.GetEnabledFeedSourcePreferenceIdsTask
import fi.kroon.vadret.domain.warning.CountDistrictPreferenceEntityTask
import fi.kroon.vadret.domain.warning.CountFeedSourcePreferenceEntityTask
import fi.kroon.vadret.domain.warning.SetDefaultDistrictPreferenceEntityService
import fi.kroon.vadret.domain.warning.SetDefaultFeedSourcePreferenceEntityService
import fi.kroon.vadret.presentation.shared.IViewModel
import fi.kroon.vadret.presentation.warning.display.di.WarningScope
import fi.kroon.vadret.presentation.warning.display.model.IWarningModel
import fi.kroon.vadret.util.extension.asObservable
import io.github.sphrak.either.Either
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import javax.inject.Inject
import timber.log.Timber

@WarningScope
class WarningViewModel @Inject constructor(
    private var state: WarningView.State,
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
) : IViewModel {

    operator fun invoke(): ObservableTransformer<WarningView.Event, WarningView.State> = onEvent

    private val onEvent = ObservableTransformer<WarningView.Event,
        WarningView.State> { upstream: Observable<WarningView.Event> ->
        upstream.publish { shared: Observable<WarningView.Event> ->
            Observable.mergeArray(
                shared.ofType(WarningView.Event.OnFailureHandled::class.java),
                shared.ofType(WarningView.Event.OnViewInitialised::class.java),
                shared.ofType(WarningView.Event.OnProgressBarEffectStarted::class.java),
                shared.ofType(WarningView.Event.OnProgressBarEffectStopped::class.java),
                shared.ofType(WarningView.Event.OnNoWarningsIssuedDisplayed::class.java),
                shared.ofType(WarningView.Event.OnSwipedToRefresh::class.java),
                shared.ofType(WarningView.Event.OnWarningListDisplayed::class.java),
                shared.ofType(WarningView.Event.OnScrollPositionRestored::class.java),
                shared.ofType(WarningView.Event.OnStateParcelUpdated::class.java),
                shared.ofType(WarningView.Event.OnFilterButtonToggled::class.java)
            ).compose(
                eventToViewState
            )
        }
    }

    private val eventToViewState = ObservableTransformer<WarningView.Event, WarningView.State> { upstream: Observable<WarningView.Event> ->
        upstream.flatMap { event: WarningView.Event ->
            when (event) {
                is WarningView.Event.OnViewInitialised -> onViewInitialisedEvent(event)
                WarningView.Event.OnFailureHandled -> onFailureHandledEvent()
                WarningView.Event.OnProgressBarEffectStarted -> onProgressBarEffectStartedEvent()
                WarningView.Event.OnProgressBarEffectStopped -> onProgressBarEffectStoppedEvent()
                WarningView.Event.OnSwipedToRefresh -> onSwipedToRefreshEvent()
                WarningView.Event.OnWarningListDisplayed -> onAlertListDisplayedEvent()
                WarningView.Event.OnScrollPositionRestored -> onScrollPositionRestoredEvent()
                WarningView.Event.OnStateParcelUpdated -> onStateParcelUpdatedEvent()
                WarningView.Event.OnFilterButtonToggled -> onFilterButtonToggled()
                WarningView.Event.OnNoWarningsIssuedDisplayed -> onNoWarningsIssuedDisplayed()
            }
        }
    }

    private fun onNoWarningsIssuedDisplayed(): Observable<WarningView.State> =
        endLoadingWarning()

    private fun preLoadingWarning(): Observable<WarningView.State> =
        when {
            state.startRefreshing -> {
                state = state.copy(
                    renderEvent = WarningView.RenderEvent.StartProgressBarEffect,
                    startRefreshing = false,
                    stopRefreshing = true
                )
                state.asObservable()
            }
            else -> {
                Timber.d("PRE LOADING AGGREGATED FEED END")
                state = state.copy(
                    isInitialised = true
                )
                loadDistrict()
            }
        }

    private fun onFilterButtonToggled(): Observable<WarningView.State> {
        val renderEvent: WarningView.RenderEvent.NavigateToFilter =
            WarningView.RenderEvent.NavigateToFilter
        state = state.copy(renderEvent = renderEvent)
        return state.asObservable()
    }

    private fun setDefaultFeedSourcePreference(): Observable<WarningView.State> =
        countFeedSourcePreferenceEntityTask()
            .flatMapObservable { result: Either<Failure, Boolean> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("Failure: $failure")
                        state.asObservable()
                    },
                    { feedSourceListAvailable: Boolean ->
                        Timber.d("FEED SOURCE PREFS ARE AVAILABLE: $feedSourceListAvailable")
                        when (feedSourceListAvailable) {
                            /**
                             *  [loadLastChecked] starts loading preferences
                             *  needed to complete aggregated feed request.
                             */
                            true -> loadLastChecked()
                            false -> setDefaultFeedSourcePreferenceEntityService()
                                .flatMapObservable { result: Either<Failure, Boolean> ->
                                    result.either(
                                        { failure: Failure ->
                                            Timber.e("Failure: $failure")
                                            state.asObservable()
                                        },
                                        { feedSourceIsAvailable: Boolean ->
                                            when (feedSourceIsAvailable) {
                                                true -> loadLastChecked()
                                                false -> {
                                                    Timber.e("This is a deeply erronous state, this should really not happen")
                                                    state.asObservable()
                                                }
                                            }
                                        }
                                    )
                                }
                        }
                    }
                )
            }

    private fun setDefaultDistrictPreference(): Observable<WarningView.State> =
        countDistrictPreferenceEntityTask()
            .flatMapObservable { result: Either<Failure, Boolean> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("Failure: $failure")
                        state.asObservable()
                    },
                    { districtPreferenceAvailable: Boolean ->
                        Timber.d("DISTRICT PREFS ARE AVAILABLE: $districtPreferenceAvailable")
                        when (districtPreferenceAvailable) {
                            true -> setDefaultFeedSourcePreference()
                            false -> setDefaultDistrictPreferenceEntityService()
                                .flatMapObservable { result: Either<Failure, Boolean> ->
                                    result.either(
                                        { failure: Failure ->
                                            Timber.e("Failure: $failure")
                                            state.asObservable()
                                        },
                                        { districtPreferencesAvailable: Boolean ->
                                            when (districtPreferencesAvailable) {
                                                true -> setDefaultFeedSourcePreference()
                                                false -> {
                                                    Timber.e("This is a deeply erronous state, this should really not happen")
                                                    state.asObservable()
                                                }
                                            }
                                        }
                                    )
                                }
                        }
                    }
                )
            }

    private fun endLoadingWarning(): Observable<WarningView.State> =
        when {
            state.wasRestoredFromStateParcel -> {
                state = state.copy(
                    renderEvent = WarningView.RenderEvent.RestoreScrollPosition,
                    wasRestoredFromStateParcel = false
                )
                state.asObservable()
            }
            state.stopRefreshing -> {
                state = state.copy(
                    renderEvent = WarningView.RenderEvent.StopProgressBarEffect,
                    stopRefreshing = false
                )
                state.asObservable()
            }
            else -> {
                Timber.d("endLoadingWarning ended.")
                updateStateParcel()
            }
        }

    private fun restoreStateFromStateParcel(stateParcel: WarningView.StateParcel?) {
        Timber.d("restoreStateFromStateParcel: $stateParcel")
        stateParcel?.run {
            state = state.copy(
                forceNet = forceNet,
                wasRestoredFromStateParcel = true,
                startRefreshing = startRefreshing,
                stopRefreshing = stopRefreshing
            )
        }
    }

    private fun updateStateParcel(): Observable<WarningView.State> {
        Timber.d("updateStateParcel")
        state = state.copy(
            renderEvent = WarningView.RenderEvent.UpdateStateParcel,
            startRefreshing = false,
            stopRefreshing = false,
            forceNet = false
        )
        return state.asObservable()
    }

    private fun updateStateToLoadingAndRefreshing(): Observable<WarningView.State> {
        Timber.d("updateStateToLoadingAndRefreshing")
        state = state.copy(
            startRefreshing = state.wasRestoredFromStateParcel.not()
        )
        return state.asObservable()
    }

    private fun updateStateToRefreshing(): Observable<WarningView.State> {
        Timber.d("updateStateToRefreshing")
        state = state.copy(
            startRefreshing = true
        )
        return state.asObservable()
    }

    private fun onViewInitialisedEvent(event: WarningView.Event.OnViewInitialised): Observable<WarningView.State> {
        restoreStateFromStateParcel(event.stateParcel)
        updateStateToLoadingAndRefreshing()
        return preLoadingWarning()
    }

    fun loadLastChecked(): Observable<WarningView.State> =
        getAggregatedFeedLastCheckedTask()
            .flatMapObservable { result: Either<Failure, Long> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("failure: $failure")
                        state.asObservable()
                    },
                    { timeStamp: Long ->
                        loadDistrictIds(timeStamp = timeStamp)
                    }
                )
            }

    private fun loadDistrictIds(timeStamp: Long): Observable<WarningView.State> =
        getEnabledDistrictPreferenceIdsTask()
            .flatMapObservable { result: Either<Failure, List<Int>> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("failure: $failure")
                        state.asObservable()
                    },
                    { districtIds: List<Int> ->
                        loadFeedSourceIds(
                            districtIds = districtIds,
                            timeStamp = timeStamp
                        )
                    }
                )
            }

    private fun loadFeedSourceIds(timeStamp: Long, districtIds: List<Int>): Observable<WarningView.State> =
        getEnabledFeedSourcePreferenceIdsTask()
            .flatMapObservable { result: Either<Failure, List<Int>> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("failure: $failure")
                        state.asObservable()
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

    fun loadAggregatedFeed(timeStamp: Long, districtIds: List<Int>, feedSourceIds: List<Int>): Observable<WarningView.State> =
        getAggregatedFeedService(
            timeStamp = timeStamp,
            forceNet = state.forceNet,
            districtIds = districtIds,
            feedSourceIds = feedSourceIds
        ).flatMapObservable { result: Either<Failure, GetAggregatedFeedService.Data> ->
            result.either(
                { failure: Failure ->
                    Timber.e("Failure: $failure")
                    val errorCode: Int = getErrorCode(failure)
                    val renderEvent: WarningView.RenderEvent.DisplayError = WarningView.RenderEvent.DisplayError(errorCode)

                    state = state.copy(renderEvent = renderEvent)
                    state.asObservable()
                },
                { data: GetAggregatedFeedService.Data ->

                    Timber.d("WARNING LIST RAW: ${data.aggregatedFeedList}")
                    val warningModelList: MutableList<IWarningModel> = WarningMapper(entityList = data.aggregatedFeedList)
                    Timber.d("WARNING LIST: $warningModelList")

                    val renderEvent: WarningView.RenderEvent = if (data.aggregatedFeedList.isNotEmpty()) {
                        WarningView.RenderEvent.DisplayAggregatedFeed(list = warningModelList)
                    } else {
                        WarningView.RenderEvent.DisplayNoWarningsIssued
                    }
                    state = state.copy(renderEvent = renderEvent)

                    setAggregatedFeedLastCheckedTask(data.timeStamp)
                        .flatMapObservable { result ->
                            result.either(
                                { failure: Failure ->
                                    Timber.e("failure: $failure")
                                    state.asObservable()
                                },
                                {
                                    state.asObservable()
                                }
                            )
                        }
                }
            )
        }

    private fun loadDistrict(): Observable<WarningView.State> =
        getDistrictNetService()
            .flatMapObservable { result: Either<Failure, Boolean> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("failure: $failure")
                        state.asObservable()
                    },
                    { districtIsAvailable: Boolean ->
                        when (districtIsAvailable) {
                            true -> loadFeedSource()
                            false -> {
                                val errorCode: Int = getErrorCode(DistrictFailure.DistrictNotAvailable)
                                val renderEvent: WarningView.RenderEvent.DisplayError = WarningView.RenderEvent.DisplayError(errorCode)
                                state = state.copy(renderEvent = renderEvent)
                                state.asObservable()
                            }
                        }
                    }
                )
            }

    private fun loadFeedSource(): Observable<WarningView.State> =
        getFeedSourceNetService()
            .flatMapObservable { result ->
                result.either(
                    { failure: Failure ->
                        Timber.e("failure: $failure")
                        state.asObservable()
                    },
                    { feedSourceIsAvailable: Boolean ->
                        when (feedSourceIsAvailable) {
                            true -> setDefaultDistrictPreference()
                            false -> {
                                val errorCode: Int = getErrorCode(DistrictFailure.DistrictNotAvailable)
                                val renderEvent: WarningView.RenderEvent.DisplayError = WarningView.RenderEvent.DisplayError(errorCode)
                                state = state.copy(renderEvent = renderEvent)
                                state.asObservable()
                            }
                        }
                    }
                )
            }

    private fun onFailureHandledEvent(): Observable<WarningView.State> = endLoadingWarning()

    private fun onProgressBarEffectStartedEvent(): Observable<WarningView.State> {
        return preLoadingWarning()
    }

    private fun onProgressBarEffectStoppedEvent(): Observable<WarningView.State> {
        return endLoadingWarning()
    }

    private fun onSwipedToRefreshEvent(): Observable<WarningView.State> {
        Timber.d("SWIPED ALERT REFRESH")
        state = state.copy(forceNet = true)
        updateStateToRefreshing()
        return preLoadingWarning()
    }

    private fun onAlertListDisplayedEvent(): Observable<WarningView.State> {
        return endLoadingWarning()
    }

    private fun onScrollPositionRestoredEvent(): Observable<WarningView.State> {
        return endLoadingWarning()
    }

    private fun onStateParcelUpdatedEvent(): Observable<WarningView.State> {
        state = state.copy(
            renderEvent = WarningView.RenderEvent.None
        )
        return state.asObservable()
    }
}