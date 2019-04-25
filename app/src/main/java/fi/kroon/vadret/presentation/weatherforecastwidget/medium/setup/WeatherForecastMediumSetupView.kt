package fi.kroon.vadret.presentation.weatherforecastwidget.medium.setup

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.recyclerview.widget.DiffUtil
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.utils.DEFAULT_COUNTY
import fi.kroon.vadret.utils.DEFAULT_LATITUDE
import fi.kroon.vadret.utils.DEFAULT_LOCALITY
import fi.kroon.vadret.utils.DEFAULT_LONGITUDE
import fi.kroon.vadret.utils.DEFAULT_MUNICIPALITY
import fi.kroon.vadret.utils.DEFAULT_THEME
import fi.kroon.vadret.utils.DEFAULT_UPDATE_INTERVAL
import fi.kroon.vadret.utils.extensions.empty
import kotlinx.android.parcel.Parcelize

object WeatherForecastMediumSetupView {

    sealed class Event {
        data class OnSetupInitialised(val appWidgetId: Int, val stateParcel: StateParcel?) : Event()
        object OnLocalityTextUpdated : Event()
        object OnLocationPermissionDenied : Event()
        object OnSavedInstanceStateUpdated : Event()
        data class OnForecastFormatSelected(val position: Int) : Event()
        data class OnThemeSelected(val position: Int) : Event()
        data class OnUpdateIntervalSelected(val position: Int) : Event()
        data class OnPhonePositionToggled(val toggled: Boolean) : Event()
        data class OnConfigurationConfirmed(val appWidgetId: Int) : Event()
        object OnCanceledClicked : Event()
        object OnLocalitySearchEnabled : Event()
        object OnLocalitySearchDisabled : Event()
        object OnSearchViewDismissed : Event()
        data class OnSearchButtonSubmitted(val text: String) : Event()
        data class OnAutoCompleteItemClicked(val autoCompleteItem: AutoCompleteItem) : Event()
        data class OnSearchTextChanged(val text: String) : Event()
    }

    data class State(
        val renderEvent: RenderEvent = RenderEvent.None,
        val locality: String? = null,
        val theme: String = DEFAULT_THEME,
        val updateInterval: String = DEFAULT_UPDATE_INTERVAL,
        val forecastFormat: Int = 0,
        val usePhonesPosition: Boolean = false,
        val searchText: String = String.empty(),
        val autoCompleteItem: AutoCompleteItem = AutoCompleteItem(
            county = DEFAULT_COUNTY,
            latitude = DEFAULT_LATITUDE
                .toDouble(),
            longitude = DEFAULT_LONGITUDE
                .toDouble(),
            locality = DEFAULT_LOCALITY,
            municipality = DEFAULT_MUNICIPALITY
        )
    )

    sealed class RenderEvent {
        object None : RenderEvent()
        object EnableLocalitySearch : RenderEvent()
        object ResetLocalitySearch : RenderEvent()
        object DisableLocalitySearch : RenderEvent()
        object TurnOffPhonePositionSwitch : RenderEvent()
        class ConfirmConfiguration(val updateIntervalMillis: Long) : RenderEvent()
        class UpdateSelectedLocalityText(val locality: String) : RenderEvent()
        object UpdateSavedInstanceState : RenderEvent()
        object FinishActivity : RenderEvent()
        class DisplayAutoComplete(val diffResult: DiffUtil.DiffResult?, val newFilteredList: List<AutoCompleteItem>) : RenderEvent()
        class DisplayError(@StringRes val errorCode: Int) : RenderEvent()
    }

    @Parcelize
    data class StateParcel(
        val locality: String? = null,
        val theme: String,
        val updateInterval: String,
        val usePhonesPosition: Boolean,
        val forecastFormat: Int,
        val searchText: String,
        val autoCompleteItem: AutoCompleteItem
    ) : Parcelable
}