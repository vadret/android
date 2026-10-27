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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import fi.kroon.vadret.forecastwarning.ui.WarningRoute
import fi.kroon.vadret.main.navigation.RadarKey
import fi.kroon.vadret.main.navigation.SettingsKey
import fi.kroon.vadret.main.navigation.TopLevelDestination
import fi.kroon.vadret.main.navigation.VadretNavigationBar
import fi.kroon.vadret.main.navigation.WarningKey
import fi.kroon.vadret.main.navigation.WeatherKey
import fi.kroon.vadret.settings.ui.SettingsRoute
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.weatherforecast.ui.WeatherForecastRoute
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

private val navigationConfiguration: SavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(baseClass = NavKey::class) {
            subclass(subclass = WeatherKey::class, serializer = WeatherKey.serializer())
            subclass(subclass = WarningKey::class, serializer = WarningKey.serializer())
            subclass(subclass = RadarKey::class, serializer = RadarKey.serializer())
            subclass(subclass = SettingsKey::class, serializer = SettingsKey.serializer())
        }
    }
}

@Composable
internal fun VadretApp(
    radarTab: RadarTab?,
    modifier: Modifier = Modifier
) {
    val backStack: NavBackStack<NavKey> = rememberNavBackStack(navigationConfiguration, WeatherKey)
    val currentKey: NavKey? = backStack.lastOrNull()
    val destinations: ImmutableList<TopLevelDestination> = remember(radarTab) {
        TopLevelDestination.entries
            .filter { destination: TopLevelDestination -> destination != TopLevelDestination.RADAR || radarTab != null }
            .toImmutableList()
    }

    Scaffold(
        bottomBar = {
            VadretNavigationBar(
                destinations = destinations,
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
        RadarHost(radarTab = radarTab) {
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
                    if (radarTab != null) {
                        entry<RadarKey> { radarTab.content() }
                    }
                    entry<SettingsKey> { SettingsRoute() }
                },
                modifier = Modifier
                    .padding(paddingValues = innerPadding)
                    .consumeWindowInsets(paddingValues = innerPadding)
            )
        }
    }
}

@Composable
private fun RadarHost(
    radarTab: RadarTab?,
    content: @Composable () -> Unit
) {
    if (radarTab != null) {
        radarTab.host(content)
    } else {
        content()
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