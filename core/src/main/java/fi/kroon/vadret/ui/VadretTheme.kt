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

private val LocalVadretColors = staticCompositionLocalOf { VadretColors() }
private val LocalVadretSpacing = staticCompositionLocalOf { VadretSpacing() }

private val accent = Color(color = 0xFF6779CA)

/**
 *  Cards use the medium shape and have nearly square corners.
 */
private val vadretShapes = Shapes(medium = RoundedCornerShape(size = 2.dp))

private val baseTypography = Typography()

/**
 *  Text without the letter spacing Material 3 adds,
 *  as in the view based app this replaced.
 */
private val vadretTypography = Typography(
    bodyLarge = baseTypography.bodyLarge.copy(letterSpacing = 0.sp),
    bodyMedium = baseTypography.bodyMedium.copy(letterSpacing = 0.sp),
    bodySmall = baseTypography.bodySmall.copy(letterSpacing = 0.sp),
    titleMedium = baseTypography.titleMedium.copy(letterSpacing = 0.sp),
    labelLarge = baseTypography.labelLarge.copy(letterSpacing = 0.sp)
)
private val mutedText = Color(color = 0xFF727272)
private val darkText = Color(color = 0xFF2B2B2B)
private val lightText = Color(color = 0xFFE6E6E6)
private val lightHint = Color(color = 0xFFCECECE)

private val lightScheme: ColorScheme = lightColorScheme(
    primary = accent,
    onPrimary = Color.White,
    primaryContainer = accent,
    onPrimaryContainer = Color.White,
    secondaryContainer = Color(color = 0xFFE3E7F7),
    onSecondaryContainer = darkText,
    background = Color(color = 0xFFF7FBFF),
    onBackground = darkText,
    surface = Color(color = 0xFFFDFDFD),
    onSurface = darkText,
    onSurfaceVariant = mutedText,
    surfaceContainer = Color.White,
    surfaceContainerLow = Color.White
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

/**
 *  The three themes the settings screen offers, with the
 *  colors of the original View based themes.
 */
@Composable
fun VadretTheme(
    theme: Theme,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalVadretColors provides VadretColors(),
        LocalVadretSpacing provides VadretSpacing()
    ) {
        MaterialTheme(
            colorScheme = theme.toColorScheme(),
            shapes = vadretShapes,
            typography = vadretTypography,
            content = content
        )
    }
}

/**
 *  Read from composables the same way as [MaterialTheme].
 */
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