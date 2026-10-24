package fi.kroon.vadret.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import fi.kroon.vadret.main.navigation.WarningKey
import fi.kroon.vadret.main.navigation.WeatherKey
import fi.kroon.vadret.radar.ui.RadarRoute
import fi.kroon.vadret.settings.ui.SettingsRoute
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.weatherforecast.ui.WeatherForecastRoute

private val navigationIconSize = 24.dp

@Composable
internal fun VadretApp(modifier: Modifier = Modifier) {
    val backStack: NavBackStack<NavKey> = rememberNavBackStack(WeatherKey)
    val currentKey: NavKey? = backStack.lastOrNull()

    Scaffold(
        bottomBar = {
            Column {
                HorizontalDivider()
                NavigationBar {
                    TopLevelDestination.entries.forEach { destination: TopLevelDestination ->
                        val isSelected: Boolean = destination.key == currentKey
                        val label: String = stringResource(id = destination.labelResourceId)
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                selectTab(
                                    backStack = backStack,
                                    key = destination.key
                                )
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(id = destination.iconResourceId),
                                    contentDescription = null,
                                    modifier = Modifier.size(size = navigationIconSize)
                                )
                            },
                            label = { Text(text = label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = VadretTheme.colorScheme.primary,
                                selectedTextColor = VadretTheme.colorScheme.primary,
                                unselectedIconColor = VadretTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = VadretTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets(0.dp),
        modifier = modifier
    ) { innerPadding: PaddingValues ->
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
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