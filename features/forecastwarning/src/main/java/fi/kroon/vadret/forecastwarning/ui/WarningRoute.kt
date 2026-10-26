package fi.kroon.vadret.forecastwarning.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fi.kroon.vadret.core.R
import fi.kroon.vadret.di.koinViewModel
import fi.kroon.vadret.forecastwarning.WarningEvent
import fi.kroon.vadret.forecastwarning.WarningState
import fi.kroon.vadret.forecastwarning.WarningViewModel
import fi.kroon.vadret.forecastwarning.model.WarningFilterState
import fi.kroon.vadret.forecastwarning.model.WarningItem
import fi.kroon.vadret.theme.model.Theme
import fi.kroon.vadret.ui.ThemePreviewParameterProvider
import fi.kroon.vadret.ui.VadretProgressBar
import fi.kroon.vadret.ui.VadretPullToRefreshBox
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.ui.VadretTopAppBar

private const val WARNING_CONTENT_TYPE = "warning"

@Composable
fun WarningRoute(
    modifier: Modifier = Modifier,
    viewModel: WarningViewModel = koinViewModel()
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
internal fun WarningScreen(
    state: WarningState,
    onEvent: (WarningEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        onEvent(WarningEvent.OnStart)
    }
    val errorMessageResourceId: Int? = state.errorMessageResourceId
    if (errorMessageResourceId != null) {
        val message: String = stringResource(id = errorMessageResourceId)
        LaunchedEffect(errorMessageResourceId) {
            snackbarHostState.showSnackbar(message = message)
            onEvent(WarningEvent.OnErrorShown)
        }
    }

    Scaffold(
        topBar = {
            VadretTopAppBar {
                IconButton(onClick = { onEvent(WarningEvent.OnFilterButtonClicked) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_filter_list),
                        contentDescription = stringResource(id = R.string.filter_warnings),
                        tint = VadretTheme.colorScheme.onBackground
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = VadretTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(),
        modifier = modifier
    ) { innerPadding: PaddingValues ->
        VadretPullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onEvent(WarningEvent.OnRefresh) },
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
                items(
                    items = state.warnings,
                    key = { warning: WarningItem -> warning.identifier },
                    contentType = { WARNING_CONTENT_TYPE }
                ) { warning: WarningItem ->
                    WarningCard(warning = warning)
                }
            }
            if (state.isLoading) {
                VadretProgressBar(modifier = Modifier.align(alignment = Alignment.TopCenter))
            }
            if (state.isEmpty) {
                Text(
                    text = stringResource(id = R.string.warning_display_no_warnings_issued),
                    style = VadretTheme.textStyles.body,
                    color = VadretTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(alignment = Alignment.Center)
                        .padding(all = VadretTheme.spacing.extraLarge)
                )
            }
        }
    }

    val filter: WarningFilterState? = state.filter
    if (filter != null) {
        WarningFilterSheet(
            filter = filter,
            onEvent = onEvent
        )
    }
}

@Preview(device = Devices.PHONE)
@Composable
private fun WarningScreenPreview(
    @PreviewParameter(provider = ThemePreviewParameterProvider::class) theme: Theme
) {
    VadretTheme(theme = theme) {
        WarningScreen(
            state = warningPreviewState(),
            onEvent = {}
        )
    }
}