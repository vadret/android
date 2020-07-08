package fi.kroon.vadret.presentation.warning.filter

import fi.kroon.vadret.R
import fi.kroon.vadret.data.district.model.DistrictOptionEntity
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsource.model.FeedSourceOptionEntity
import fi.kroon.vadret.domain.districtpreference.UpdateDistrictPreferenceListTask
import fi.kroon.vadret.domain.feedsourcepreference.UpdateFeedSourcePreferenceListTask
import fi.kroon.vadret.domain.warningfilter.GetWarningFilterOptionListService
import fi.kroon.vadret.presentation.shared.IViewModel
import fi.kroon.vadret.presentation.warning.filter.di.WarningFilterScope
import fi.kroon.vadret.presentation.warning.filter.model.IFilterable
import fi.kroon.vadret.util.extension.asObservable
import io.github.sphrak.either.Either
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import timber.log.Timber
import javax.inject.Inject

@WarningFilterScope
class WarningFilterViewModel @Inject constructor(
    private var state: WarningFilterView.State,
    private val getWarningFilterOptionListService: GetWarningFilterOptionListService,
    private val updateDistrictPreferenceEntityTask: UpdateDistrictPreferenceListTask,
    private val updateFeedSourcePreferenceEntityTask: UpdateFeedSourcePreferenceListTask
) : IViewModel {

    operator fun invoke(): ObservableTransformer<WarningFilterView.Event, WarningFilterView.State> = onEvent

    private val onEvent = ObservableTransformer<WarningFilterView.Event,
        WarningFilterView.State> { upstream: Observable<WarningFilterView.Event> ->
        upstream.publish { shared: Observable<WarningFilterView.Event> ->
            Observable.mergeArray(
                shared.ofType(WarningFilterView.Event.OnViewInitialised::class.java),
                shared.ofType(WarningFilterView.Event.OnFeedSourceItemSelected::class.java),
                shared.ofType(WarningFilterView.Event.OnDistrictItemSelected::class.java),
                shared.ofType(WarningFilterView.Event.OnFilterOptionsDisplayed::class.java),
                shared.ofType(WarningFilterView.Event.OnFilterOptionsApplyClicked::class.java)
            ).compose(
                eventToViewState
            )
        }
    }

    private val eventToViewState =
        ObservableTransformer<WarningFilterView.Event, WarningFilterView.State> { upstream: Observable<WarningFilterView.Event> ->
            upstream.flatMap { event: WarningFilterView.Event ->
                when (event) {
                    is WarningFilterView.Event.OnViewInitialised -> onViewInitialisedEvent(event.stateParcel)
                    is WarningFilterView.Event.OnFeedSourceItemSelected -> onFeedSourceItemSelected(event.entity)
                    is WarningFilterView.Event.OnDistrictItemSelected -> onDistrictItemSelected(event.entity)
                    WarningFilterView.Event.OnFilterOptionsApplyClicked -> onFilterOptionsApplyClicked()
                    WarningFilterView.Event.OnFilterOptionsDisplayed -> onFilterOptionsDisplayed()
                }
            }
        }

    private fun onViewInitialisedEvent(stateParcel: WarningFilterView.StateParcel?): Observable<WarningFilterView.State> {
        restoreStateFromStateParcel(stateParcel)
        return onViewInitialised()
    }

    private fun onFilterOptionsDisplayed(): Observable<WarningFilterView.State> {
        Timber.d("ON FILTER OPTIONS DISPLAYED")
        state = state.copy(renderEvent = WarningFilterView.RenderEvent.None)
        return state.asObservable()
    }

    private fun onViewInitialised(): Observable<WarningFilterView.State> =
        getWarningFilterOptionListService()
            .flatMapObservable { result: Either<Failure, GetWarningFilterOptionListService.Data> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("Failure: $failure")
                        state.asObservable()
                    },
                    { data: GetWarningFilterOptionListService.Data ->

                        Timber.d("GET WARNING FILTER OPTION LIST")
                        val list: List<IFilterable> = WarningFilterMapper(
                            districtOptionEntityList = data.districtOptionList,
                            feedSourceOptionEntityList = data.feedSourceOptionList
                        )

                        val renderEvent: WarningFilterView.RenderEvent.DisplayFilterList =
                            WarningFilterView.RenderEvent.DisplayFilterList(list = list)

                        state = state.copy(
                            renderEvent = renderEvent,
                            districtOptionList = data.districtOptionList.toMutableList(),
                            feedSourceOptionList = data.feedSourceOptionList.toMutableList()
                        )
                        state.asObservable()
                    }
                )
            }

    private fun onFilterOptionsApplyClicked(): Observable<WarningFilterView.State> {
        Timber.d("ON APPLY FILTER CLICKED")

        val enabledDistrictList: List<DistrictOptionEntity> = state.districtOptionList
            .filter { district: DistrictOptionEntity ->
                district.isEnabled
            }

        val enabledFeedSourceList = state.feedSourceOptionList
            .filter { feedSource: FeedSourceOptionEntity ->
                feedSource.isEnabled
            }

        return when (enabledDistrictList.isNotEmpty() && enabledFeedSourceList.isNotEmpty()) {
            true -> updateDistrictPreferenceEntity()
            false -> renderValidationError()
        }
    }

    private fun updateDistrictPreferenceEntity(): Observable<WarningFilterView.State> =
        updateDistrictPreferenceEntityTask(entityList = state.districtOptionList)
            .flatMapObservable { result: Either<Failure, Int> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("Failure: $failure")
                        state.asObservable()
                    },
                    { rowCount: Int ->
                        Timber.d("DistrictPreference updated: $rowCount")
                        updateFeedSourcePreferenceEntity()
                    }
                )
            }

    private fun updateFeedSourcePreferenceEntity(): Observable<WarningFilterView.State> =
        updateFeedSourcePreferenceEntityTask(entityList = state.feedSourceOptionList)
            .flatMapObservable { result: Either<Failure, Int> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("Failure: $failure")
                        state.asObservable()
                    },
                    { rowCount: Int ->
                        Timber.d("FeedSourcePreference updated: $rowCount")
                        state = state.copy(renderEvent = WarningFilterView.RenderEvent.FinishDialog)
                        state.asObservable()
                    }
                )
            }

    private fun renderValidationError(): Observable<WarningFilterView.State> {
        state = state.copy(renderEvent = WarningFilterView.RenderEvent.DisplayError(R.string.filter_must_select_one))
        return state.asObservable()
    }

    private fun onFeedSourceItemSelected(entity: FeedSourceOptionEntity): Observable<WarningFilterView.State> {

        Timber.d("FEED SOURCE ITEM SELECTED: $entity")
        val feedSourceOptionEntity: FeedSourceOptionEntity? = state
            .feedSourceOptionList
            .find { fields: FeedSourceOptionEntity ->
                fields.id == entity.id
            }

        feedSourceOptionEntity?.let {

            val newFeedSourceOptionEntity = FeedSourceOptionEntity(
                id = it.id,
                feedSourceId = it.feedSourceId,
                isEnabled = it.isEnabled.not(),
                usedBy = it.usedBy,
                name = it.name
            )

            val entityIndex: Int = state.feedSourceOptionList.indexOf(feedSourceOptionEntity)

            state.feedSourceOptionList.removeAt(entityIndex)
            state.feedSourceOptionList.add(entityIndex, newFeedSourceOptionEntity)

            val list: List<IFilterable> = WarningFilterMapper(
                districtOptionEntityList = state.districtOptionList,
                feedSourceOptionEntityList = state.feedSourceOptionList
            )

            val renderEvent: WarningFilterView.RenderEvent =
                WarningFilterView.RenderEvent.UpdateFilterList(list = list)

            state = state.copy(renderEvent = renderEvent)

            return state.asObservable()
        }

        state = state.copy(renderEvent = WarningFilterView.RenderEvent.None)
        return state.asObservable()
    }

    private fun onDistrictItemSelected(entity: DistrictOptionEntity): Observable<WarningFilterView.State> {
        Timber.d("DISTRICT ITEM SELECTED: $entity")
        val districtOptionEntity: DistrictOptionEntity? = state
            .districtOptionList
            .find { fields: DistrictOptionEntity ->
                fields.id == entity.id
            }

        val entityIndex: Int = state.districtOptionList.indexOf(districtOptionEntity)

        districtOptionEntity?.let {
            val newDistrictOptionEntity = DistrictOptionEntity(
                id = it.id,
                districtId = it.districtId,
                isEnabled = it.isEnabled.not(),
                usedBy = it.usedBy,
                category = it.category,
                name = it.name
            )

            state.districtOptionList.removeAt(entityIndex)
            state.districtOptionList.add(
                entityIndex,
                newDistrictOptionEntity
            )

            val list: List<IFilterable> = WarningFilterMapper(
                districtOptionEntityList = state.districtOptionList,
                feedSourceOptionEntityList = state.feedSourceOptionList
            )

            val renderEvent: WarningFilterView.RenderEvent =
                WarningFilterView.RenderEvent.UpdateFilterList(list = list)

            state = state.copy(renderEvent = renderEvent)

            return state.asObservable()
        }

        state = state.copy(renderEvent = WarningFilterView.RenderEvent.None)
        return state.asObservable()
    }

    private fun restoreStateFromStateParcel(stateParcel: WarningFilterView.StateParcel?) {
        stateParcel?.run { ->
            state = state.copy(
                districtOptionList = districtOptionList,
                feedSourceOptionList = feedSourceOptionList
            )
        }
    }
}