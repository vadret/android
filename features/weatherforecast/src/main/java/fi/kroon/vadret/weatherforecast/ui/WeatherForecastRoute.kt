package fi.kroon.vadret.weatherforecast.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fi.kroon.vadret.core.R
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.ui.VadretTopAppBar
import fi.kroon.vadret.weatherforecast.WeatherForecastEvent
import fi.kroon.vadret.weatherforecast.WeatherForecastState
import fi.kroon.vadret.weatherforecast.WeatherForecastViewModel
import fi.kroon.vadret.weatherforecast.di.weatherForecastViewModel
import fi.kroon.vadret.weatherforecast.model.SearchState
import fi.kroon.vadret.weatherforecast.model.WeatherForecastRow

private const val SEARCH_KEY = "search"
private const val HEADLINE_CONTENT_TYPE = "headline"
private const val SUMMARY_CONTENT_TYPE = "summary"
private const val DAY_CONTENT_TYPE = "day"
private const val HOUR_CONTENT_TYPE = "hour"

/**
 *  Keeps the last card clear of the search button.
 */
private val floatingActionButtonClearance = 88.dp

@Composable
fun WeatherForecastRoute(
    modifier: Modifier = Modifier,
    viewModel: WeatherForecastViewModel = weatherForecastViewModel()
) {
    val state: WeatherForecastState by viewModel.state.collectAsStateWithLifecycle()
    WeatherForecastScreen(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherForecastScreen(
    state: WeatherForecastState,
    onEvent: (WeatherForecastEvent) -> Unit,
    modifier: Modifier = Modifier
) {
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
    Scaffold(
        topBar = { VadretTopAppBar(trailingText = state.localityName) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            if (search == null) {
                FloatingActionButton(
                    onClick = { onEvent(WeatherForecastEvent.OnSearchButtonClicked) },
                    shape = CircleShape,
                    containerColor = VadretTheme.colorScheme.primary,
                    contentColor = VadretTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search_white_24dp),
                        contentDescription = stringResource(id = R.string.search)
                    )
                }
            }
        },
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
                    start = VadretTheme.spacing.small,
                    end = VadretTheme.spacing.small,
                    top = VadretTheme.spacing.small,
                    bottom = floatingActionButtonClearance
                ),
                verticalArrangement = Arrangement.spacedBy(space = VadretTheme.spacing.small),
                modifier = Modifier.fillMaxSize()
            ) {
                if (search != null) {
                    item(key = SEARCH_KEY, contentType = SEARCH_KEY) {
                        SearchPanel(
                            search = search,
                            onEvent = onEvent
                        )
                    }
                } else {
                    forecastRows(rows = state.forecast.overview)
                }
                forecastRows(rows = state.forecast.days)
            }
            if (state.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(alignment = Alignment.TopCenter)
                )
            }
        }
    }
}

private fun LazyListScope.forecastRows(rows: List<WeatherForecastRow>) {
    items(
        items = rows,
        key = { row: WeatherForecastRow -> row.key },
        contentType = { row: WeatherForecastRow -> contentType(row = row) }
    ) { row: WeatherForecastRow ->
        when (row) {
            is WeatherForecastRow.Headline -> HeadlineCard(row = row)
            is WeatherForecastRow.Summary -> SummaryCard(row = row)
            is WeatherForecastRow.Day -> DayHeader(row = row)
            is WeatherForecastRow.Hour -> HourCard(row = row)
        }
    }
}

private fun contentType(row: WeatherForecastRow): String =
    when (row) {
        is WeatherForecastRow.Headline -> HEADLINE_CONTENT_TYPE
        is WeatherForecastRow.Summary -> SUMMARY_CONTENT_TYPE
        is WeatherForecastRow.Day -> DAY_CONTENT_TYPE
        is WeatherForecastRow.Hour -> HOUR_CONTENT_TYPE
    }