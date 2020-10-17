package fi.kroon.vadret.presentation.warning.display

import android.os.Parcelable
import androidx.annotation.StringRes
import fi.kroon.vadret.presentation.warning.display.model.IWarningModel
import kotlinx.android.parcel.Parcelize

object WarningView {

    sealed class Event {
        data class OnViewInitialised(val stateParcel: StateParcel?) : Event()
        object OnFailureHandled : Event()
        object OnNoWarningsIssuedDisplayed : Event()
        object OnProgressBarEffectStarted : Event()
        object OnProgressBarEffectStopped : Event()
        object OnSwipedToRefresh : Event()
        object OnFilterButtonToggled : Event()
        object OnWarningListDisplayed : Event()
        object OnScrollPositionRestored : Event()
        object OnStateParcelUpdated : Event()
        data class OnWarningFilterResult(val result: String) : Event()
    }

    data class State(
        val forceNet: Boolean = false,
        val isInitialised: Boolean = false,
        val renderEvent: RenderEvent = WarningView.RenderEvent.None,
        val startRefreshing: Boolean = false,
        val stopRefreshing: Boolean = false,
        val wasRestoredFromStateParcel: Boolean = false
    )

    sealed class RenderEvent {
        object None : RenderEvent()
        object StartProgressBarEffect : RenderEvent()
        object StopProgressBarEffect : RenderEvent()
        object UpdateStateParcel : RenderEvent()
        object RestoreScrollPosition : RenderEvent()
        object NavigateToFilter : RenderEvent()
        object DisplayNoWarningsIssued : RenderEvent()
        class DisplayAggregatedFeed(val list: MutableList<IWarningModel>) : RenderEvent()
        class DisplayError(@StringRes val errorCode: Int) : RenderEvent()
    }

    @Parcelize
    data class StateParcel(
        val forceNet: Boolean,
        val startRefreshing: Boolean,
        val stopRefreshing: Boolean
    ) : Parcelable
}