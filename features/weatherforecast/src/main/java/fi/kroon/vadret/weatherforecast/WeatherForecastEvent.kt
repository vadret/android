package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.autocomplete.model.AutoCompleteItem

sealed interface WeatherForecastEvent {

    /**
     *  Sent each time the screen is shown, the location
     *  mode may have changed in settings meanwhile.
     */
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