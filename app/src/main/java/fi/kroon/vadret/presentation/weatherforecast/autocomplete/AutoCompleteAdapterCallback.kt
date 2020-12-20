package fi.kroon.vadret.presentation.weatherforecast.autocomplete

import fi.kroon.vadret.presentation.weatherforecast.WeatherForecastView

interface AutoCompleteAdapterCallback {
    fun onAutoCompleteItemClicked(event: WeatherForecastView.Event.OnAutoCompleteItemClicked)
}