package fi.kroon.vadret.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fi.kroon.vadret.core.R
import fi.kroon.vadret.di.koinViewModel
import fi.kroon.vadret.settings.SettingsEvent
import fi.kroon.vadret.settings.SettingsState
import fi.kroon.vadret.settings.SettingsViewModel
import fi.kroon.vadret.ui.VadretTopAppBar
import kotlinx.coroutines.launch

private enum class SettingsPage(val titleResourceId: Int) {
    PREFERENCES(titleResourceId = R.string.settings),
    LIBRARIES(titleResourceId = R.string.about_app_libraries_tab_title),
    ABOUT(titleResourceId = R.string.about_app_about_title)
}

@Composable
fun SettingsRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state: SettingsState by viewModel.state.collectAsStateWithLifecycle()
    SettingsScreen(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )
}

@Composable
fun SettingsScreen(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState: PagerState = rememberPagerState(pageCount = { SettingsPage.entries.size })
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        onEvent(SettingsEvent.OnStart)
    }

    Column(modifier = modifier.fillMaxSize()) {
        VadretTopAppBar()
        PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
            SettingsPage.entries.forEach { page: SettingsPage ->
                Tab(
                    selected = pagerState.currentPage == page.ordinal,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(page = page.ordinal) }
                    },
                    text = { Text(text = stringResource(id = page.titleResourceId)) }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex: Int ->
            when (SettingsPage.entries[pageIndex]) {
                SettingsPage.PREFERENCES -> PreferencesPage(
                    state = state,
                    onEvent = onEvent
                )

                SettingsPage.LIBRARIES -> LibrariesPage(libraries = state.libraries)

                SettingsPage.ABOUT -> AboutPage(aboutRows = state.aboutRows)
            }
        }
    }

    val settings = state.settings
    if (state.isThemeDialogVisible && settings != null) {
        ThemeDialog(
            selectedTheme = settings.theme,
            onThemeSelected = { theme -> onEvent(SettingsEvent.OnThemeSelected(theme = theme)) },
            onDismiss = { onEvent(SettingsEvent.OnThemeDialogDismissed) }
        )
    }
}