package fi.kroon.vadret.weatherforecast

import androidx.annotation.StringRes
import fi.kroon.vadret.weatherforecast.model.SearchState
import fi.kroon.vadret.weatherforecast.model.WeatherForecast

/**
 *  @param localityName the place the forecast is for.
 *  @param isLocationPermissionRequested asks the screen to
 *  request location access, answered with
 *  [WeatherForecastEvent.OnLocationPermissionResult].
 *  @param search set while the search field is open.
 */
data class WeatherForecastState(
    val forecast: WeatherForecast = WeatherForecast(
        overview = emptyList(),
        days = emptyList()
    ),
    val localityName: String? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isLocationPermissionRequested: Boolean = false,
    val search: SearchState? = null,
    @StringRes
    val errorMessageResourceId: Int? = null
)