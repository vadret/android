package fi.kroon.vadret.weatherforecast

import androidx.compose.runtime.Immutable
import fi.kroon.vadret.weatherforecast.model.ForecastView
import fi.kroon.vadret.weatherforecast.model.LocationChoices
import fi.kroon.vadret.weatherforecast.model.SearchState
import fi.kroon.vadret.weatherforecast.model.WeatherForecast
import org.jetbrains.compose.resources.StringResource

@Immutable
data class WeatherForecastState(
    val forecast: WeatherForecast = WeatherForecast(),
    val localityName: String? = null,
    val locationChoices: LocationChoices = LocationChoices(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isLocationPermissionRequested: Boolean = false,
    val search: SearchState? = null,
    val forecastView: ForecastView = ForecastView.DAYS,
    val errorMessageResource: StringResource? = null
)