package fi.kroon.vadret.weatherforecast

import androidx.annotation.StringRes
import fi.kroon.vadret.weatherforecast.model.SearchState
import fi.kroon.vadret.weatherforecast.model.WeatherForecast
import kotlinx.collections.immutable.persistentListOf

data class WeatherForecastState(
    val forecast: WeatherForecast = WeatherForecast(
        overview = persistentListOf(),
        days = persistentListOf()
    ),
    val localityName: String? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isLocationPermissionRequested: Boolean = false,
    val search: SearchState? = null,
    @StringRes
    val errorMessageResourceId: Int? = null
)