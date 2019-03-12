package fi.kroon.vadret.presentation.weatherforecast

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.recyclerview.widget.DiffUtil
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.data.nominatim.model.Locality
import fi.kroon.vadret.presentation.weatherforecast.model.BaseWeatherForecastModel
import fi.kroon.vadret.utils.extensions.empty
import kotlinx.android.parcel.Parcelize

object WeatherForecastView {

    sealed class Event {
        class OnViewInitialised(val stateParcel: StateParcel?) : Event()
        object OnLocationPermissionDenied : Event()
        object OnLocationPermissionGranted : Event()
        object OnLocationPermissionDeniedNeverAskAgain : Event()
        object OnSearchButtonToggled : Event()
        object OnSearchViewDismissed : Event()
        object OnShimmerEffectStarted : Event()
        object OnShimmerEffectStopped : Event()
        object OnProgressBarEffectStarted : Event()
        object OnProgressBarEffectStopped : Event()
        object OnScrollPositionRestored : Event()
        object OnWeatherListDisplayed : Event()
        object OnStateParcelUpdated : Event()
        object OnSwipedToRefresh : Event()
        object OnFailureHandled : Event()
        data class OnSearchButtonSubmitted(val query: String) : Event()
        data class OnAutoCompleteItemClicked(val autoCompleteItem: AutoCompleteItem) : Event()
        data class OnSearchTextChanged(val text: String) : Event()
    }

    data class State(
        val forceNet: Boolean = false,
        val isInitialised: Boolean = false,
        val isSearchToggled: Boolean = false,
        val renderEvent: RenderEvent = RenderEvent.None,
        val searchText: String = String.empty(),
        val startLoading: Boolean = false,
        val startRefreshing: Boolean = false,
        val stopLoading: Boolean = false,
        val stopRefreshing: Boolean = false,
        val timeStamp: Long? = null,
        val wasRestoredFromStateParcel: Boolean = false
    )

    sealed class RenderEvent {
        object None : RenderEvent()
        object RequestLocationPermission : RenderEvent()
        object StartShimmerEffect : RenderEvent()
        object StartProgressBarEffect : RenderEvent()
        object StopShimmerEffect : RenderEvent()
        object StopProgressBarEffect : RenderEvent()
        object UpdateStateParcel : RenderEvent()
        object RestoreScrollPosition : RenderEvent()
        object EnableSearchView : RenderEvent()
        class DisableSearchView(val text: String) : RenderEvent()
        class DisplayAutoComplete(val diffResult: DiffUtil.DiffResult?, val newFilteredList: List<AutoCompleteItem>) : RenderEvent()
        class DisplayWeatherForecast(val list: List<BaseWeatherForecastModel>, val locality: Locality) : RenderEvent()
        class DisplayError(@StringRes val errorCode: Int) : RenderEvent()
    }

    @Parcelize
    data class StateParcel(
        val forceNet: Boolean,
        val isSearchToggled: Boolean,
        val searchText: String = String.empty(),
        val startLoading: Boolean,
        val startRefreshing: Boolean,
        val stopLoading: Boolean,
        val stopRefreshing: Boolean,
        val timeStamp: Long? = null
    ) : Parcelable
}