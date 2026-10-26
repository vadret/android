package fi.kroon.vadret.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fi.kroon.vadret.di.koinViewModel
import fi.kroon.vadret.library.model.Library
import fi.kroon.vadret.settings.SettingsEvent
import fi.kroon.vadret.settings.SettingsState
import fi.kroon.vadret.settings.SettingsViewModel
import fi.kroon.vadret.theme.model.Theme
import fi.kroon.vadret.ui.ThemePreviewParameterProvider
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.ui.VadretTopAppBar
import kotlinx.coroutines.launch

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
internal fun SettingsScreen(
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
        SettingsTabs(
            selectedPage = SettingsPage.entries[pagerState.targetPage],
            onPageClick = { page: SettingsPage ->
                coroutineScope.launch { pagerState.animateScrollToPage(page = page.ordinal) }
            },
            modifier = Modifier.padding(horizontal = VadretTheme.spacing.large)
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex: Int ->
            when (SettingsPage.entries[pageIndex]) {
                SettingsPage.PREFERENCES -> PreferencesPage(
                    state = state,
                    onEvent = onEvent
                )

                SettingsPage.LIBRARIES -> LibrariesPage(
                    libraryGroups = state.libraryGroups,
                    onLibraryClick = { library: Library ->
                        onEvent(SettingsEvent.OnLibraryClicked(library = library))
                    }
                )

                SettingsPage.ABOUT -> AboutPage(aboutRows = state.aboutRows)
            }
        }
    }

    val selectedLibrary: Library? = state.selectedLibrary
    if (selectedLibrary != null) {
        LibrarySheet(
            library = selectedLibrary,
            onDismiss = { onEvent(SettingsEvent.OnLibraryDismissed) }
        )
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

@Preview(device = Devices.PHONE)
@Composable
private fun SettingsScreenPreview(
    @PreviewParameter(provider = ThemePreviewParameterProvider::class) theme: Theme
) {
    VadretTheme(theme = theme) {
        SettingsScreen(
            state = settingsPreviewState(theme = theme),
            onEvent = {},
            modifier = Modifier.background(color = VadretTheme.colorScheme.background)
        )
    }
}