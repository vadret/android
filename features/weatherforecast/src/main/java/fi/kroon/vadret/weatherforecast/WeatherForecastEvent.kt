package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.autocomplete.model.AutoCompleteItem

sealed interface WeatherForecastEvent {

    data object OnShown : WeatherForecastEvent
    data object OnRefresh : WeatherForecastEvent
    data object OnErrorShown : WeatherForecastEvent
    data class OnLocationPermissionResult(val isGranted: Boolean) : WeatherForecastEvent
    data object OnSearchButtonClicked : WeatherForecastEvent
    data object OnSearchDismissed : WeatherForecastEvent
    data object OnSearchSubmitted : WeatherForecastEvent
    data class OnSearchQueryChanged(val query: String) : WeatherForecastEvent
    data class OnSuggestionClicked(val autoCompleteItem: AutoCompleteItem) : WeatherForecastEvent
}