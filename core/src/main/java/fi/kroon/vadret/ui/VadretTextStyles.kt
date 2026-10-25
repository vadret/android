package fi.kroon.vadret.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private const val TABULAR_NUMBERS = "tnum"

internal fun TextStyle.withTabularNumbers(): TextStyle = copy(fontFeatureSettings = TABULAR_NUMBERS)

private fun textStyle(
    fontSize: Int,
    lineHeight: Int,
    fontWeight: FontWeight = FontWeight.Normal
): TextStyle =
    TextStyle(
        fontSize = fontSize.sp,
        lineHeight = lineHeight.sp,
        fontWeight = fontWeight,
        letterSpacing = 0.sp
    ).withTabularNumbers()

@Immutable
data class VadretTextStyles(
    val temperature: TextStyle = textStyle(
        fontSize = 64,
        lineHeight = 64,
        fontWeight = FontWeight.Light
    ).copy(letterSpacing = (-2).sp),
    val title: TextStyle = textStyle(
        fontSize = 20,
        lineHeight = 26,
        fontWeight = FontWeight.Medium
    ),
    val heading: TextStyle = textStyle(
        fontSize = 18,
        lineHeight = 24,
        fontWeight = FontWeight.Medium
    ),
    val value: TextStyle = textStyle(
        fontSize = 16,
        lineHeight = 22,
        fontWeight = FontWeight.Medium
    ),
    val body: TextStyle = textStyle(
        fontSize = 15,
        lineHeight = 22
    ),
    val bodyStrong: TextStyle = textStyle(
        fontSize = 15,
        lineHeight = 22,
        fontWeight = FontWeight.Medium
    ),
    val detail: TextStyle = textStyle(
        fontSize = 14,
        lineHeight = 20
    ),
    val detailStrong: TextStyle = textStyle(
        fontSize = 14,
        lineHeight = 20,
        fontWeight = FontWeight.Medium
    ),
    val detailLabel: TextStyle = textStyle(
        fontSize = 13,
        lineHeight = 20
    ),
    val label: TextStyle = textStyle(
        fontSize = 12,
        lineHeight = 16
    ),
    val labelStrong: TextStyle = textStyle(
        fontSize = 12,
        lineHeight = 16,
        fontWeight = FontWeight.Bold
    ),
    val attribution: TextStyle = textStyle(
        fontSize = 10,
        lineHeight = 14
    )
)