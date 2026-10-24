package fi.kroon.vadret.main

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fi.kroon.vadret.data.theme.model.Theme
import fi.kroon.vadret.main.di.mainViewModel
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.ui.isDark

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = mainViewModel()
            val theme: Theme? by viewModel.theme.collectAsStateWithLifecycle()
            val currentTheme: Theme = theme ?: return@setContent
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
    }

    /**
     *  The app theme is chosen in settings, so the system
     *  bar icons follow it rather than the system setting.
     */
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