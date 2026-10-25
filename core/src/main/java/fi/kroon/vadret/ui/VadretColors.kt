package fi.kroon.vadret.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class VadretColors(
    val cardBorder: Color,
    val divider: Color,
    val icon: Color,
    val chipBorder: Color,
    val mapAttribution: Color = Color(color = 0xD9FFFFFF),
    val onMapAttribution: Color = Color(color = 0xFF3A3D46),
    val onMapLabel: Color = Color(color = 0xFF1B1C21),
    val warningMessage: Color = Color(color = 0xFFDFE3F3),
    val onWarningMessage: Color = Color(color = 0xFF263056),
    val warningYellow: Color = Color(color = 0xFFFFE27A),
    val onWarningYellow: Color = Color(color = 0xFF3D3000),
    val warningOrange: Color = Color(color = 0xFFFFC08A),
    val onWarningOrange: Color = Color(color = 0xFF3D1C00),
    val warningRed: Color = Color(color = 0xFFFFA59B),
    val onWarningRed: Color = Color(color = 0xFF410C08),
    val districtLand: Color = Color(color = 0xFFCDE7CA),
    val districtLandStroke: Color = Color(color = 0xFFB1C9AD),
    val districtSea: Color = Color(color = 0xFFD2DFF8),
    val districtSeaStroke: Color = Color(color = 0xFFBBC9E6),
    val krisinformation: Color = Color(color = 0xFFD2DFF8),
    val krisinformationStroke: Color = Color(color = 0xFFBBC9E6),
    val smhi: Color = Color(color = 0xFFCDE7CA),
    val smhiStroke: Color = Color(color = 0xFFB1C9AD),
    val unknownSource: Color = Color(color = 0xFFD0C6DF),
    val chipText: Color = Color(color = 0xFF2B2B2B)
)

internal val lightVadretColors: VadretColors = VadretColors(
    cardBorder = Color(color = 0xFFE7E9F0),
    divider = Color(color = 0xFFEEF0F4),
    icon = Color(color = 0xFF4A4F5E),
    chipBorder = Color(color = 0xFFD9DCE6)
)

internal val darkVadretColors: VadretColors = VadretColors(
    cardBorder = Color(color = 0xFF4A4A4A),
    divider = Color(color = 0xFF484848),
    icon = Color(color = 0xFFCECECE),
    chipBorder = Color(color = 0xFF5A5A5A)
)

internal val amoledVadretColors: VadretColors = VadretColors(
    cardBorder = Color(color = 0xFF262626),
    divider = Color(color = 0xFF1E1E1E),
    icon = Color(color = 0xFFCECECE),
    chipBorder = Color(color = 0xFF333333)
)