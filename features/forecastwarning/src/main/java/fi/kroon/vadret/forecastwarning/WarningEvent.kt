package fi.kroon.vadret.forecastwarning

import fi.kroon.vadret.aggregatedfeed.model.FeedSource

sealed interface WarningEvent {
    data object OnStart : WarningEvent
    data object OnRefresh : WarningEvent
    data object OnErrorShown : WarningEvent
    data object OnFilterButtonClicked : WarningEvent
    data object OnFilterDismissed : WarningEvent
    data object OnFilterApplied : WarningEvent
    data class OnFeedSourceToggled(val feedSource: FeedSource) : WarningEvent
    data class OnDistrictToggled(val districtId: Int) : WarningEvent
}