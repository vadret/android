package fi.kroon.vadret.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class VadretColors(
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