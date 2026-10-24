package fi.kroon.vadret.forecastwarning

sealed interface WarningEvent {
    data object OnRefresh : WarningEvent
    data object OnErrorShown : WarningEvent
    data object OnFilterButtonClicked : WarningEvent
    data object OnFilterDismissed : WarningEvent
    data object OnFilterApplied : WarningEvent
    data class OnFeedSourceToggled(val id: Int) : WarningEvent
    data class OnDistrictToggled(val id: Int) : WarningEvent
}