package fi.kroon.vadret.presentation.warning.filter

import android.os.Parcelable
import fi.kroon.vadret.data.district.model.DistrictOptionEntity
import fi.kroon.vadret.data.feedsource.model.FeedSourceOptionEntity
import fi.kroon.vadret.presentation.warning.filter.model.IFilterable
import kotlinx.android.parcel.Parcelize

object WarningFilterView {

    sealed class Event {
        class OnViewInitialised(val stateParcel: StateParcel?) : Event()
        class OnFeedSourceItemSelected(val entity: FeedSourceOptionEntity) : Event()
        class OnDistrictItemSelected(val entity: DistrictOptionEntity) : Event()
        object OnFilterOptionsDisplayed : Event()
        object OnFilterOptionsApplyClicked : Event()
    }

    data class State(
        val forceNet: Boolean = false,
        val districtOptionList: MutableList<DistrictOptionEntity> = mutableListOf(),
        val feedSourceOptionList: MutableList<FeedSourceOptionEntity> = mutableListOf(),
        val renderEvent: RenderEvent = WarningFilterView.RenderEvent.None
    )

    sealed class RenderEvent {
        object None : RenderEvent()
        object FinishDialog : RenderEvent()
        class DisplayError(val errorCode: Int) : RenderEvent()
        class UpdateFilterList(val list: List<IFilterable>) : RenderEvent()
        class DisplayFilterList(val list: List<IFilterable>) : RenderEvent()
    }

    @Parcelize
    data class StateParcel(
        val forceNet: Boolean,
        val districtOptionList: MutableList<DistrictOptionEntity>,
        val feedSourceOptionList: MutableList<FeedSourceOptionEntity>
    ) : Parcelable
}