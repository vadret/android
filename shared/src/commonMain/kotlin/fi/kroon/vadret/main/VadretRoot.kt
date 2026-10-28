package fi.kroon.vadret.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fi.kroon.vadret.di.koinViewModel
import fi.kroon.vadret.language.model.AppLanguage
import fi.kroon.vadret.main.navigation.TabNavigation
import fi.kroon.vadret.main.navigation.TopLevelDestination
import fi.kroon.vadret.theme.model.Theme
import fi.kroon.vadret.ui.LanguageEnvironment
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.ui.isDark
import kotlinx.collections.immutable.ImmutableList

@Composable
fun VadretRoot(
    radarTab: RadarTab?,
    systemBarsEffect: @Composable (isDark: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: MainViewModel = koinViewModel()
    val theme: Theme? by viewModel.theme.collectAsStateWithLifecycle()
    val language: AppLanguage? by viewModel.language.collectAsStateWithLifecycle()
    val destinations: ImmutableList<TopLevelDestination> = remember(radarTab) { topLevelDestinations(isRadarShown = radarTab != null) }
    val tabNavigation: TabNavigation = rememberTabNavigation(destinations = destinations)
    LaunchedEffect(Unit) {
        viewModel.onEvent(event = MainEvent.OnStart)
    }
    val currentTheme: Theme = theme ?: return
    val currentLanguage: AppLanguage = language ?: return
    systemBarsEffect(currentTheme.isDark())
    LanguageEnvironment(languageTag = currentLanguage.languageTag) {
        VadretTheme(theme = currentTheme) {
            VadretApp(
                tabNavigation = tabNavigation,
                destinations = destinations,
                radarTab = radarTab,
                modifier = modifier
            )
        }
    }
}