package fi.kroon.vadret.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import fi.kroon.vadret.di.koinViewModel
import fi.kroon.vadret.language.model.AppLanguage
import fi.kroon.vadret.theme.model.Theme
import fi.kroon.vadret.ui.LanguageEnvironment
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.ui.isDark

@Composable
fun VadretRoot(
    radarTab: RadarTab?,
    systemBarsEffect: @Composable (isDark: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: MainViewModel = koinViewModel()
    val theme: Theme? by viewModel.theme.collectAsStateWithLifecycle()
    val language: AppLanguage? by viewModel.language.collectAsStateWithLifecycle()
    val backStack: NavBackStack<NavKey> = rememberVadretBackStack()
    LaunchedEffect(Unit) {
        viewModel.onEvent(event = MainEvent.OnStart)
    }
    val currentTheme: Theme = theme ?: return
    val currentLanguage: AppLanguage = language ?: return
    systemBarsEffect(currentTheme.isDark())
    LanguageEnvironment(languageTag = currentLanguage.languageTag) {
        VadretTheme(theme = currentTheme) {
            VadretApp(
                backStack = backStack,
                radarTab = radarTab,
                modifier = modifier
            )
        }
    }
}