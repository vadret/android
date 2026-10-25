package fi.kroon.vadret.main

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import fi.kroon.vadret.forecastwarning.ui.WarningRoute
import fi.kroon.vadret.main.navigation.RadarKey
import fi.kroon.vadret.main.navigation.SettingsKey
import fi.kroon.vadret.main.navigation.TopLevelDestination
import fi.kroon.vadret.main.navigation.VadretNavigationBar
import fi.kroon.vadret.main.navigation.WarningKey
import fi.kroon.vadret.main.navigation.WeatherKey
import fi.kroon.vadret.radar.ui.KeepRadarMapAlive
import fi.kroon.vadret.radar.ui.RadarRoute
import fi.kroon.vadret.settings.ui.SettingsRoute
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.weatherforecast.ui.WeatherForecastRoute

@Composable
internal fun VadretApp(modifier: Modifier = Modifier) {
    val backStack: NavBackStack<NavKey> = rememberNavBackStack(WeatherKey)
    val currentKey: NavKey? = backStack.lastOrNull()

    Scaffold(
        bottomBar = {
            VadretNavigationBar(
                currentKey = currentKey,
                onDestinationClick = { destination: TopLevelDestination ->
                    selectTab(
                        backStack = backStack,
                        key = destination.key
                    )
                }
            )
        },
        containerColor = VadretTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp),
        modifier = modifier
    ) { innerPadding: PaddingValues ->
        KeepRadarMapAlive {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                transitionSpec = { instantTransition() },
                popTransitionSpec = { instantTransition() },
                predictivePopTransitionSpec = { instantTransition() },
                entryProvider = entryProvider {
                    entry<WeatherKey> { WeatherForecastRoute() }
                    entry<WarningKey> { WarningRoute() }
                    entry<RadarKey> { RadarRoute() }
                    entry<SettingsKey> { SettingsRoute() }
                },
                modifier = Modifier
                    .padding(paddingValues = innerPadding)
                    .consumeWindowInsets(paddingValues = innerPadding)
            )
        }
    }
}

private fun instantTransition(): ContentTransform = EnterTransition.None togetherWith ExitTransition.None

private fun selectTab(
    backStack: NavBackStack<NavKey>,
    key: NavKey
) {
    if (backStack.lastOrNull() == key) {
        return
    }
    while (backStack.size > 1) {
        backStack.removeAt(backStack.lastIndex)
    }
    if (key != WeatherKey) {
        backStack.add(key)
    }
}