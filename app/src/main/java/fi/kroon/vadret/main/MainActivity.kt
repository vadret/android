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
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fi.kroon.vadret.BaseApplication
import fi.kroon.vadret.di.LocalKoin
import fi.kroon.vadret.di.koinViewModel
import fi.kroon.vadret.theme.model.Theme
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.ui.isDark
import org.koin.core.Koin

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val koin: Koin = (application as BaseApplication).koinApplication.koin
        setContent {
            CompositionLocalProvider(LocalKoin provides koin) {
                ThemedApp()
            }
        }
    }

    @Composable
    private fun ThemedApp() {
        val viewModel: MainViewModel = koinViewModel()
        val theme: Theme? by viewModel.theme.collectAsStateWithLifecycle()
        val currentTheme: Theme = theme ?: return
        val isDark: Boolean = currentTheme.isDark()

        DisposableEffect(isDark) {
            enableEdgeToEdge(
                statusBarStyle = systemBarStyle(isDark = isDark),
                navigationBarStyle = systemBarStyle(isDark = isDark)
            )
            onDispose {}
        }

        VadretTheme(theme = currentTheme) {
            VadretApp()
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