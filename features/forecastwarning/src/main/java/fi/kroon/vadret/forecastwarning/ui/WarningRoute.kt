package fi.kroon.vadret.forecastwarning.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fi.kroon.vadret.core.R
import fi.kroon.vadret.forecastwarning.WarningEvent
import fi.kroon.vadret.forecastwarning.WarningState
import fi.kroon.vadret.forecastwarning.WarningViewModel
import fi.kroon.vadret.forecastwarning.di.warningViewModel
import fi.kroon.vadret.forecastwarning.model.WarningFilter
import fi.kroon.vadret.forecastwarning.model.WarningItem
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.ui.VadretTopAppBar

private const val WARNING_CONTENT_TYPE = "warning"

/**
 *  Keeps the last card clear of the filter button.
 */
private val floatingActionButtonClearance = 88.dp

@Composable
fun WarningRoute(
    modifier: Modifier = Modifier,
    viewModel: WarningViewModel = warningViewModel()
) {
    val state: WarningState by viewModel.state.collectAsStateWithLifecycle()
    WarningScreen(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarningScreen(
    state: WarningState,
    onEvent: (WarningEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val errorMessageResourceId: Int? = state.errorMessageResourceId
    if (errorMessageResourceId != null) {
        val message: String = stringResource(id = errorMessageResourceId)
        LaunchedEffect(errorMessageResourceId) {
            snackbarHostState.showSnackbar(message = message)
            onEvent(WarningEvent.OnErrorShown)
        }
    }

    Scaffold(
        topBar = { VadretTopAppBar() },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(WarningEvent.OnFilterButtonClicked) },
                shape = CircleShape,
                containerColor = VadretTheme.colorScheme.primary,
                contentColor = VadretTheme.colorScheme.onPrimary
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_filter_list_black_24dp),
                    contentDescription = stringResource(id = R.string.filter_result_label)
                )
            }
        },
        modifier = modifier
    ) { innerPadding: PaddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onEvent(WarningEvent.OnRefresh) },
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
                items(
                    items = state.warnings,
                    key = { warning: WarningItem -> warning.identifier },
                    contentType = { WARNING_CONTENT_TYPE }
                ) { warning: WarningItem ->
                    WarningCard(warning = warning)
                }
            }
            if (state.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(alignment = Alignment.TopCenter)
                )
            }
            if (state.isEmpty) {
                Text(
                    text = stringResource(id = R.string.warning_display_no_warnings_issued),
                    style = VadretTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(alignment = Alignment.Center)
                        .padding(all = VadretTheme.spacing.extraLarge)
                )
            }
        }
    }

    val filter: WarningFilter? = state.filter
    if (filter != null) {
        WarningFilterSheet(
            filter = filter,
            onEvent = onEvent
        )
    }
}