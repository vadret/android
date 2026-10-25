package fi.kroon.vadret.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.kroon.vadret.theme.model.Theme

private val LocalVadretColors = staticCompositionLocalOf { lightVadretColors }
private val LocalVadretSpacing = staticCompositionLocalOf { VadretSpacing() }
private val LocalVadretTextStyles = staticCompositionLocalOf { VadretTextStyles() }

private val accent = Color(color = 0xFF6779CA)
private val lightAccent = Color(color = 0xFF3F51B5)

private val vadretShapes = Shapes(
    medium = RoundedCornerShape(size = 2.dp),
    large = RoundedCornerShape(size = 20.dp)
)

private val baseTypography = Typography()

private val vadretTypography = Typography(
    displayLarge = baseTypography.displayLarge.withTabularNumbers(),
    displayMedium = baseTypography.displayMedium.withTabularNumbers(),
    displaySmall = baseTypography.displaySmall.withTabularNumbers(),
    headlineLarge = baseTypography.headlineLarge.withTabularNumbers(),
    headlineMedium = baseTypography.headlineMedium.withTabularNumbers(),
    headlineSmall = baseTypography.headlineSmall.withTabularNumbers(),
    titleLarge = baseTypography.titleLarge.withTabularNumbers(),
    titleMedium = baseTypography.titleMedium.copy(letterSpacing = 0.sp).withTabularNumbers(),
    titleSmall = baseTypography.titleSmall.withTabularNumbers(),
    bodyLarge = baseTypography.bodyLarge.copy(letterSpacing = 0.sp).withTabularNumbers(),
    bodyMedium = baseTypography.bodyMedium.copy(letterSpacing = 0.sp).withTabularNumbers(),
    bodySmall = baseTypography.bodySmall.copy(letterSpacing = 0.sp).withTabularNumbers(),
    labelLarge = baseTypography.labelLarge.copy(letterSpacing = 0.sp).withTabularNumbers(),
    labelMedium = baseTypography.labelMedium.withTabularNumbers(),
    labelSmall = baseTypography.labelSmall.withTabularNumbers()
)
private val mutedText = Color(color = 0xFF5A5E6B)
private val darkText = Color(color = 0xFF1B1C21)
private val lightText = Color(color = 0xFFE6E6E6)
private val lightHint = Color(color = 0xFFCECECE)

private val lightScheme: ColorScheme = lightColorScheme(
    primary = lightAccent,
    onPrimary = Color.White,
    primaryContainer = lightAccent,
    onPrimaryContainer = Color.White,
    secondaryContainer = Color(color = 0xFFE3E6F8),
    onSecondaryContainer = darkText,
    background = Color(color = 0xFFF3F4F8),
    onBackground = darkText,
    surface = Color.White,
    onSurface = darkText,
    onSurfaceVariant = mutedText,
    outline = Color(color = 0xFFD9DCE6),
    outlineVariant = Color(color = 0xFFE7E9F0),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainer = Color.White,
    surfaceContainerHigh = Color.White,
    surfaceContainerHighest = Color(color = 0xFFE3E6F8)
)

private val darkScheme: ColorScheme = darkColorScheme(
    primary = accent,
    onPrimary = Color.White,
    primaryContainer = accent,
    onPrimaryContainer = Color.White,
    secondaryContainer = Color(color = 0xFF4A4F66),
    onSecondaryContainer = lightText,
    background = Color(color = 0xFF373737),
    onBackground = lightText,
    surface = Color(color = 0xFF3D3D3D),
    onSurface = lightText,
    onSurfaceVariant = lightHint,
    surfaceContainer = Color(color = 0xFF3D3D3D),
    surfaceContainerLow = Color(color = 0xFF585858)
)

private val amoledScheme: ColorScheme = darkColorScheme(
    primary = accent,
    onPrimary = Color.White,
    primaryContainer = accent,
    onPrimaryContainer = Color.White,
    secondaryContainer = Color(color = 0xFF2A2D3A),
    onSecondaryContainer = lightText,
    background = Color.Black,
    onBackground = lightText,
    surface = Color.Black,
    onSurface = lightText,
    onSurfaceVariant = lightHint,
    surfaceContainer = Color.Black,
    surfaceContainerLow = Color(color = 0xFF141414)
)

@Composable
fun VadretTheme(
    theme: Theme,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalVadretColors provides theme.toVadretColors(),
        LocalVadretSpacing provides VadretSpacing(),
        LocalVadretTextStyles provides VadretTextStyles()
    ) {
        MaterialTheme(
            colorScheme = theme.toColorScheme(),
            shapes = vadretShapes,
            typography = vadretTypography,
            content = content
        )
    }
}

object VadretTheme {

    val colorScheme: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography

    val shapes: Shapes
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes

    val colors: VadretColors
        @Composable
        @ReadOnlyComposable
        get() = LocalVadretColors.current

    val spacing: VadretSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalVadretSpacing.current

    val textStyles: VadretTextStyles
        @Composable
        @ReadOnlyComposable
        get() = LocalVadretTextStyles.current
}

fun Theme.isDark(): Boolean =
    when (this) {
        Theme.LIGHT -> false
        Theme.DARK -> true
        Theme.AMOLED -> true
    }

private fun Theme.toColorScheme(): ColorScheme =
    when (this) {
        Theme.LIGHT -> lightScheme
        Theme.DARK -> darkScheme
        Theme.AMOLED -> amoledScheme
    }

private fun Theme.toVadretColors(): VadretColors =
    when (this) {
        Theme.LIGHT -> lightVadretColors
        Theme.DARK -> darkVadretColors
        Theme.AMOLED -> amoledVadretColors
    }