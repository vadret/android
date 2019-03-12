package fi.kroon.vadret.presentation.alert

import android.os.Parcelable
import androidx.annotation.StringRes
import fi.kroon.vadret.presentation.alert.model.BaseWarningItemModel
import kotlinx.android.parcel.Parcelize

object AlertView {

    sealed class Event {
        class OnViewInitialised(val stateParcel: StateParcel?) : Event()
        object OnFailureHandled : Event()
        object OnShimmerEffectStarted : Event()
        object OnShimmerEffectStopped : Event()
        object OnProgressBarEffectStarted : Event()
        object OnProgressBarEffectStopped : Event()
        object OnSwipedToRefresh : Event()
        object OnAlertListDisplayed : Event()
        object OnScrollPositionRestored : Event()
        object OnStateParcelUpdated : Event()
    }

    data class State(
        val forceNet: Boolean = false,
        val isInitialised: Boolean = false,
        val renderEvent: RenderEvent = RenderEvent.None,
        val startLoading: Boolean = false,
        val startRefreshing: Boolean = false,
        val stopLoading: Boolean = false,
        val stopRefreshing: Boolean = false,
        val timeStamp: Long? = null,
        val wasRestoredFromStateParcel: Boolean = false
    )

    sealed class RenderEvent {
        object None : RenderEvent()
        object StartShimmerEffect : RenderEvent()
        object StartProgressBarEffect : RenderEvent()
        object StopShimmerEffect : RenderEvent()
        object StopProgressBarEffect : RenderEvent()
        object UpdateStateParcel : RenderEvent()
        object RestoreScrollPosition : RenderEvent()
        class DisplayAlert(val list: List<BaseWarningItemModel>) : RenderEvent()
        class DisplayError(@StringRes val errorCode: Int) : RenderEvent()
    }

    @Parcelize
    data class StateParcel(
        val forceNet: Boolean,
        val startLoading: Boolean,
        val startRefreshing: Boolean,
        val stopLoading: Boolean,
        val stopRefreshing: Boolean,
        val timeStamp: Long? = null
    ) : Parcelable
}