package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.favorite.model.Favorite
import fi.kroon.vadret.weatherforecast.model.ForecastView
import java.util.Locale

sealed interface WeatherForecastEvent {

    data object OnShown : WeatherForecastEvent
    data class OnLocaleChanged(val locale: Locale) : WeatherForecastEvent
    data object OnRefresh : WeatherForecastEvent
    data object OnErrorShown : WeatherForecastEvent
    data class OnForecastViewSelected(val forecastView: ForecastView) : WeatherForecastEvent
    data class OnDayToggled(val dayKey: String) : WeatherForecastEvent
    data class OnLocationPermissionResult(val isGranted: Boolean) : WeatherForecastEvent
    data object OnAutomaticLocationClicked : WeatherForecastEvent
    data class OnFavoriteClicked(val favorite: Favorite) : WeatherForecastEvent
    data class OnFavoriteRemoveClicked(val favoriteId: String) : WeatherForecastEvent
    data object OnAddPlaceClicked : WeatherForecastEvent
    data object OnSearchDismissed : WeatherForecastEvent
    data object OnSearchSubmitted : WeatherForecastEvent
    data class OnSearchQueryChanged(val query: String) : WeatherForecastEvent
    data class OnSuggestionClicked(val autoCompleteItem: AutoCompleteItem) : WeatherForecastEvent
}