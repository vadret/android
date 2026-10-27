package fi.kroon.vadret.main

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import fi.kroon.vadret.BaseApplication
import fi.kroon.vadret.di.LocalKoin
import fi.kroon.vadret.radar.ui.KeepRadarMapAlive
import fi.kroon.vadret.radar.ui.RadarRoute
import org.koin.core.Koin

private val radarTab = RadarTab(
    host = { content: @Composable () -> Unit -> KeepRadarMapAlive(content = content) },
    content = { RadarRoute() }
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val koin: Koin = (application as BaseApplication).koinApplication.koin
        setContent {
            CompositionLocalProvider(LocalKoin provides koin) {
                VadretRoot(
                    radarTab = radarTab,
                    systemBarsEffect = { isDark: Boolean -> SystemBarsEffect(isDark = isDark) }
                )
            }
        }
    }

    @Composable
    private fun SystemBarsEffect(isDark: Boolean) {
        DisposableEffect(isDark) {
            enableEdgeToEdge(
                statusBarStyle = systemBarStyle(isDark = isDark),
                navigationBarStyle = systemBarStyle(isDark = isDark)
            )
            onDispose {}
        }
    }

    private fun systemBarStyle(isDark: Boolean): SystemBarStyle =
        if (isDark) {
            SystemBarStyle.dark(scrim = Color.TRANSPARENT)
        } else {
            SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            )
        }
}