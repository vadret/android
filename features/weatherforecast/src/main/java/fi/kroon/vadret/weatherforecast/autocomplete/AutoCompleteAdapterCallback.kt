package fi.kroon.vadret.weatherforecast.autocomplete

import fi.kroon.vadret.weatherforecast.WeatherForecastView

interface AutoCompleteAdapterCallback {
    fun onAutoCompleteItemClicked(event: WeatherForecastView.Event.OnAutoCompleteItemClicked)
}