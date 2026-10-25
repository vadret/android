package fi.kroon.vadret.weatherforecast

import androidx.annotation.StringRes
import fi.kroon.vadret.weatherforecast.model.LocationChoices
import fi.kroon.vadret.weatherforecast.model.SearchState
import fi.kroon.vadret.weatherforecast.model.WeatherForecast

data class WeatherForecastState(
    val forecast: WeatherForecast = WeatherForecast(),
    val localityName: String? = null,
    val locationChoices: LocationChoices = LocationChoices(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isLocationPermissionRequested: Boolean = false,
    val search: SearchState? = null,
    @StringRes
    val errorMessageResourceId: Int? = null
)