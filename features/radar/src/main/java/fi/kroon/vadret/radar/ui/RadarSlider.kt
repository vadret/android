package fi.kroon.vadret.radar.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fi.kroon.vadret.ui.VadretTheme

private val thumbSize = 20.dp
private val trackHeight = 4.dp
private const val INACTIVE_TRACK_ALPHA = 0.24f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RadarSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier
) {
    val color: Color = VadretTheme.colorScheme.primary
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        thumb = {
            Box(
                modifier = Modifier
                    .size(size = thumbSize)
                    .background(
                        color = color,
                        shape = CircleShape
                    )
            )
        },
        track = { sliderState: SliderState ->
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = trackHeight)
            ) {
                drawRect(color = color.copy(alpha = INACTIVE_TRACK_ALPHA))
                drawRect(
                    color = color,
                    size = Size(
                        width = size.width * sliderState.fraction(),
                        height = size.height
                    )
                )
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
private fun SliderState.fraction(): Float {
    val span: Float = valueRange.endInclusive - valueRange.start
    return if (span > 0f) {
        ((value - valueRange.start) / span).coerceIn(
            minimumValue = 0f,
            maximumValue = 1f
        )
    } else {
        0f
    }
}