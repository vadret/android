package fi.kroon.vadret.presentation.warning.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.kroon.vadret.R
import fi.kroon.vadret.data.district.model.DistrictOptionEntity
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsource.model.FeedSourceOptionEntity
import fi.kroon.vadret.domain.districtpreference.UpdateDistrictPreferenceListTask
import fi.kroon.vadret.domain.feedsourcepreference.UpdateFeedSourcePreferenceListTask
import fi.kroon.vadret.domain.warningfilter.GetWarningFilterOptionListService
import fi.kroon.vadret.presentation.shared.IViewModel
import fi.kroon.vadret.presentation.warning.filter.model.IFilterable
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
class WarningFilterViewModel @Inject constructor(
    private var stateModel: WarningFilterView.State,
    private val state: MutableSharedFlow<WarningFilterView.State>,
    private val getWarningFilterOptionListService: GetWarningFilterOptionListService,
    private val updateDistrictPreferenceEntityTask: UpdateDistrictPreferenceListTask,
    private val updateFeedSourcePreferenceEntityTask: UpdateFeedSourcePreferenceListTask
) : ViewModel(), IViewModel {

    val viewState: SharedFlow<WarningFilterView.State> get() = state.asSharedFlow()

    fun send(event: WarningFilterView.Event) {
        viewModelScope.launch { reduce(event = event) }
    }

    private suspend fun reduce(event: WarningFilterView.Event) =
        when (event) {
            is WarningFilterView.Event.OnViewInitialised -> onViewInitialisedEvent(event.stateParcel)
            is WarningFilterView.Event.OnFeedSourceItemSelected -> onFeedSourceItemSelected(event.entity)
            is WarningFilterView.Event.OnDistrictItemSelected -> onDistrictItemSelected(event.entity)
            WarningFilterView.Event.OnFilterOptionsApplyClicked -> onFilterOptionsApplyClicked()
            WarningFilterView.Event.OnFilterOptionsDisplayed -> onFilterOptionsDisplayed()
        }

    private suspend fun onViewInitialisedEvent(stateParcel: WarningFilterView.StateParcel?) {
        restoreStateFromStateParcel(stateParcel)
        onViewInitialised()
    }

    private suspend fun onFilterOptionsDisplayed() {
        Timber.d("ON FILTER OPTIONS DISPLAYED")
        stateModel = stateModel.copy(renderEvent = WarningFilterView.RenderEvent.None)
        state.emit(stateModel)
    }

    private suspend fun onViewInitialised() = withContext(Dispatchers.IO) {
        getWarningFilterOptionListService()
            .await()
            .either(
                { failure: Failure ->
                    Timber.e("Failure: $failure")
                    state.emit(stateModel)
                },
                { data: GetWarningFilterOptionListService.Data ->

                    Timber.d("GET WARNING FILTER OPTION LIST")
                    val list: List<IFilterable> = WarningFilterMapper(
                        districtOptionEntityList = data.districtOptionList,
                        feedSourceOptionEntityList = data.feedSourceOptionList
                    )

                    val renderEvent: WarningFilterView.RenderEvent.DisplayFilterList =
                        WarningFilterView.RenderEvent.DisplayFilterList(list = list)

                    stateModel = stateModel.copy(
                        renderEvent = renderEvent,
                        districtOptionList = data.districtOptionList.toMutableList(),
                        feedSourceOptionList = data.feedSourceOptionList.toMutableList()
                    )
                    state.emit(stateModel)
                }
            )
    }

    private suspend fun onFilterOptionsApplyClicked() = withContext(Dispatchers.IO) {
        Timber.d("ON APPLY FILTER CLICKED")

        val enabledDistrictList: List<DistrictOptionEntity> = stateModel.districtOptionList
            .filter { district: DistrictOptionEntity ->
                district.isEnabled
            }

        val enabledFeedSourceList = stateModel.feedSourceOptionList
            .filter { feedSource: FeedSourceOptionEntity ->
                feedSource.isEnabled
            }

        when (enabledDistrictList.isNotEmpty() && enabledFeedSourceList.isNotEmpty()) {
            true -> updateDistrictPreferenceEntity()
            false -> renderValidationError()
        }
    }

    private suspend fun updateDistrictPreferenceEntity() = withContext(Dispatchers.IO) {
        updateDistrictPreferenceEntityTask(entityList = stateModel.districtOptionList)
            .await()
            .either(
                { failure: Failure ->
                    Timber.e("Failure: $failure")
                    state.emit(stateModel)
                },
                { rowCount: Int ->
                    Timber.d("DistrictPreference updated: $rowCount")
                    updateFeedSourcePreferenceEntity()
                }
            )
    }

    private suspend fun updateFeedSourcePreferenceEntity() = withContext(Dispatchers.IO) {
        updateFeedSourcePreferenceEntityTask(entityList = stateModel.feedSourceOptionList)
            .await()
            .either(
                { failure: Failure ->
                    Timber.e("Failure: $failure")
                    state.emit(stateModel)
                },
                { rowCount: Int ->
                    Timber.d("FeedSourcePreference updated: $rowCount")
                    stateModel =
                        stateModel.copy(renderEvent = WarningFilterView.RenderEvent.FinishDialog)
                    state.emit(stateModel)
                }
            )
    }

    private suspend fun renderValidationError() {
        stateModel = stateModel.copy(renderEvent = WarningFilterView.RenderEvent.DisplayError(R.string.filter_must_select_one))
        state.emit(stateModel)
    }

    private suspend fun onFeedSourceItemSelected(entity: FeedSourceOptionEntity) = withContext(Dispatchers.IO) {

        Timber.d("FEED SOURCE ITEM SELECTED: $entity")
        val feedSourceOptionEntity: FeedSourceOptionEntity? = stateModel
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

            val entityIndex: Int = stateModel.feedSourceOptionList.indexOf(feedSourceOptionEntity)

            stateModel.feedSourceOptionList.removeAt(entityIndex)
            stateModel.feedSourceOptionList.add(entityIndex, newFeedSourceOptionEntity)

            val list: List<IFilterable> = WarningFilterMapper(
                districtOptionEntityList = stateModel.districtOptionList,
                feedSourceOptionEntityList = stateModel.feedSourceOptionList
            )

            val renderEvent: WarningFilterView.RenderEvent =
                WarningFilterView.RenderEvent.UpdateFilterList(list = list)

            stateModel = stateModel.copy(renderEvent = renderEvent)

            state.emit(stateModel)
        }

        stateModel = stateModel.copy(renderEvent = WarningFilterView.RenderEvent.None)
        state.emit(stateModel)
    }

    private suspend fun onDistrictItemSelected(entity: DistrictOptionEntity) = withContext(Dispatchers.IO) {
        Timber.d("DISTRICT ITEM SELECTED: $entity")
        val districtOptionEntity: DistrictOptionEntity? = stateModel
            .districtOptionList
            .find { fields: DistrictOptionEntity ->
                fields.id == entity.id
            }

        val entityIndex: Int = stateModel.districtOptionList.indexOf(districtOptionEntity)

        districtOptionEntity?.let {
            val newDistrictOptionEntity = DistrictOptionEntity(
                id = it.id,
                districtId = it.districtId,
                isEnabled = it.isEnabled.not(),
                usedBy = it.usedBy,
                category = it.category,
                name = it.name
            )

            stateModel.districtOptionList.removeAt(entityIndex)
            stateModel.districtOptionList.add(
                entityIndex,
                newDistrictOptionEntity
            )

            val list: List<IFilterable> = WarningFilterMapper(
                districtOptionEntityList = stateModel.districtOptionList,
                feedSourceOptionEntityList = stateModel.feedSourceOptionList
            )

            val renderEvent: WarningFilterView.RenderEvent =
                WarningFilterView.RenderEvent.UpdateFilterList(list = list)

            stateModel = stateModel.copy(renderEvent = renderEvent)

            state.emit(stateModel)
        }

        stateModel = stateModel.copy(renderEvent = WarningFilterView.RenderEvent.None)
        state.emit(stateModel)
    }

    private fun restoreStateFromStateParcel(stateParcel: WarningFilterView.StateParcel?) {
        stateParcel?.run { ->
            stateModel = stateModel.copy(
                districtOptionList = districtOptionList,
                feedSourceOptionList = feedSourceOptionList
            )
        }
    }
}