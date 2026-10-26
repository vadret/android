package fi.kroon.vadret.weatherforecast.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fi.kroon.vadret.di.koinViewModel
import fi.kroon.vadret.ui.VadretProgressBar
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.ui.VadretTopAppBar
import fi.kroon.vadret.weatherforecast.WeatherForecastEvent
import fi.kroon.vadret.weatherforecast.WeatherForecastState
import fi.kroon.vadret.weatherforecast.WeatherForecastViewModel
import fi.kroon.vadret.weatherforecast.model.CurrentWeather
import fi.kroon.vadret.weatherforecast.model.ForecastView
import fi.kroon.vadret.weatherforecast.model.SearchState
import java.util.Locale

private const val SEARCH_KEY = "search"
private const val CURRENT_WEATHER_KEY = "current-weather"
private const val FORECAST_VIEW_KEY = "forecast-view"
private const val FORECAST_CONTENT_KEY = "forecast-content"

@Composable
fun WeatherForecastRoute(
    modifier: Modifier = Modifier,
    viewModel: WeatherForecastViewModel = koinViewModel()
) {
    val state: WeatherForecastState by viewModel.state.collectAsStateWithLifecycle()
    WeatherForecastScreen(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )
}

@Composable
internal fun WeatherForecastScreen(
    state: WeatherForecastState,
    onEvent: (WeatherForecastEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val locale: Locale = LocalConfiguration.current.locales[0]
    LaunchedEffect(locale) {
        onEvent(WeatherForecastEvent.OnLocaleChanged(locale = locale))
    }
    LaunchedEffect(Unit) {
        onEvent(WeatherForecastEvent.OnShown)
    }
    LocationPermissionEffect(
        isRequested = state.isLocationPermissionRequested,
        onResult = { isGranted: Boolean ->
            onEvent(WeatherForecastEvent.OnLocationPermissionResult(isGranted = isGranted))
        }
    )

    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val errorMessageResourceId: Int? = state.errorMessageResourceId
    if (errorMessageResourceId != null) {
        val message: String = stringResource(id = errorMessageResourceId)
        LaunchedEffect(errorMessageResourceId) {
            snackbarHostState.showSnackbar(message = message)
            onEvent(WeatherForecastEvent.OnErrorShown)
        }
    }

    val search: SearchState? = state.search
    val currentWeather: CurrentWeather? = state.forecast.current
    Scaffold(
        topBar = {
            VadretTopAppBar {
                LocationChip(
                    localityName = state.localityName,
                    locationChoices = state.locationChoices,
                    onEvent = onEvent,
                    modifier = Modifier.padding(end = VadretTheme.spacing.extraSmall)
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = VadretTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(),
        modifier = modifier
    ) { innerPadding: PaddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onEvent(WeatherForecastEvent.OnRefresh) },
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .fillMaxSize()
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = VadretTheme.spacing.medium,
                    end = VadretTheme.spacing.medium,
                    top = VadretTheme.spacing.extraSmall,
                    bottom = VadretTheme.spacing.medium
                ),
                verticalArrangement = Arrangement.spacedBy(space = VadretTheme.spacing.medium),
                modifier = Modifier.fillMaxSize()
            ) {
                if (search != null) {
                    item(
                        key = SEARCH_KEY,
                        contentType = SEARCH_KEY
                    ) {
                        SearchPanel(
                            search = search,
                            onEvent = onEvent
                        )
                    }
                } else if (currentWeather != null) {
                    item(
                        key = CURRENT_WEATHER_KEY,
                        contentType = CURRENT_WEATHER_KEY
                    ) {
                        CurrentWeatherCard(currentWeather = currentWeather)
                    }
                }
                forecast(
                    state = state,
                    onEvent = onEvent
                )
            }
            if (state.isLoading) {
                VadretProgressBar(modifier = Modifier.align(alignment = Alignment.TopCenter))
            }
        }
    }
}

private fun LazyListScope.forecast(
    state: WeatherForecastState,
    onEvent: (WeatherForecastEvent) -> Unit
) {
    if (state.forecast.days.isEmpty()) {
        return
    }
    item(
        key = FORECAST_VIEW_KEY,
        contentType = FORECAST_VIEW_KEY
    ) {
        ForecastViewSelector(
            forecastView = state.forecastView,
            onForecastViewSelected = { forecastView: ForecastView ->
                onEvent(WeatherForecastEvent.OnForecastViewSelected(forecastView = forecastView))
            }
        )
    }
    item(
        key = FORECAST_CONTENT_KEY,
        contentType = FORECAST_CONTENT_KEY
    ) {
        ForecastContent(
            forecastView = state.forecastView,
            forecast = state.forecast,
            onEvent = onEvent
        )
    }
}