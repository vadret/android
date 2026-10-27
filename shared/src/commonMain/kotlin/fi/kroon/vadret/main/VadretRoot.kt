package fi.kroon.vadret.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fi.kroon.vadret.di.koinViewModel
import fi.kroon.vadret.theme.model.Theme
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
    LaunchedEffect(Unit) {
        viewModel.onEvent(event = MainEvent.OnStart)
    }
    val currentTheme: Theme = theme ?: return
    systemBarsEffect(currentTheme.isDark())
    VadretTheme(theme = currentTheme) {
        VadretApp(
            radarTab = radarTab,
            modifier = modifier
        )
    }
}