package fi.kroon.vadret.radar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fi.kroon.vadret.core.R
import fi.kroon.vadret.radar.model.RadarLayer
import fi.kroon.vadret.ui.VadretTheme
import kotlin.math.roundToInt

private val cardElevation = 6.dp
private val cardTopPadding = 14.dp
private val cardSectionSpacing = 14.dp
private val playButtonSize = 48.dp
private val playIconSize = 28.dp
private val timelineSpacing = 6.dp
private val legendHeight = 6.dp

private val precipitationLegend: List<Color> = listOf(
    Color(color = 0xFF9CC9FF),
    Color(color = 0xFF2F6FE0),
    Color(color = 0xFF2DB34A),
    Color(color = 0xFFF2CF2E),
    Color(color = 0xFFEC5A2A)
)

private val windLegend: List<Color> = listOf(
    Color(color = 0xFF3B46A0),
    Color(color = 0xFF3E6EBE),
    Color(color = 0xFF3496AF),
    Color(color = 0xFF3CAA78),
    Color(color = 0xFF50B446),
    Color(color = 0xFFBEAF46),
    Color(color = 0xFFD28246),
    Color(color = 0xFFC34B78),
    Color(color = 0xFF963CAA)
)

@Composable
internal fun RadarTimelineCard(
    layer: RadarLayer,
    timeline: RadarTimeline,
    isPlaying: Boolean,
    onPlayClick: () -> Unit,
    onIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = VadretTheme.shapes.large,
        color = VadretTheme.colorScheme.surface,
        shadowElevation = cardElevation,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(space = cardSectionSpacing),
            modifier = Modifier.padding(
                start = VadretTheme.spacing.large,
                end = VadretTheme.spacing.large,
                top = cardTopPadding,
                bottom = VadretTheme.spacing.large
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(space = cardSectionSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlayButton(
                    isPlaying = isPlaying,
                    onClick = onPlayClick
                )
                TimelineColumn(
                    timeline = timeline,
                    onIndexChange = onIndexChange,
                    modifier = Modifier.weight(weight = 1f)
                )
            }
            Legend(layer = layer)
        }
    }
}

@Composable
private fun PlayButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconResourceId: Int = if (isPlaying) {
        R.drawable.ic_pause_filled
    } else {
        R.drawable.ic_play_arrow_filled
    }
    val contentDescriptionResourceId: Int = if (isPlaying) {
        R.string.pause
    } else {
        R.string.play
    }
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = VadretTheme.colorScheme.primary,
        contentColor = VadretTheme.colorScheme.onPrimary,
        modifier = modifier.size(size = playButtonSize)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(id = iconResourceId),
                contentDescription = stringResource(id = contentDescriptionResourceId),
                modifier = Modifier.size(size = playIconSize)
            )
        }
    }
}

@Composable
private fun TimelineColumn(
    timeline: RadarTimeline,
    onIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = timelineSpacing),
        modifier = modifier
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = timeline.timeText,
                style = VadretTheme.textStyles.heading,
                color = VadretTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(weight = 1f)
                    .alignByBaseline()
            )
            Text(
                text = timeline.caption,
                style = VadretTheme.textStyles.label,
                color = VadretTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.alignByBaseline()
            )
        }
        RadarSlider(
            value = timeline.index.toFloat(),
            onValueChange = { value: Float -> onIndexChange(value.roundToInt()) },
            valueRange = 0f..timeline.lastIndex.coerceAtLeast(minimumValue = 1).toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(timeline.startLabel, timeline.middleLabel, timeline.endLabel).forEach { label: String ->
                Text(
                    text = label,
                    style = VadretTheme.textStyles.label,
                    color = VadretTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun Legend(
    layer: RadarLayer,
    modifier: Modifier = Modifier
) {
    val colors: List<Color>
    val startLabel: String
    val endLabel: String
    when (layer) {
        RadarLayer.PRECIPITATION -> {
            colors = precipitationLegend
            startLabel = stringResource(id = R.string.precipitation_light)
            endLabel = stringResource(id = R.string.precipitation_heavy)
        }

        RadarLayer.WIND -> {
            colors = windLegend
            startLabel = stringResource(id = R.string.wind_speed_minimum)
            endLabel = stringResource(id = R.string.wind_speed_maximum)
        }
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = VadretTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = startLabel,
            style = VadretTheme.textStyles.label,
            color = VadretTheme.colorScheme.onSurfaceVariant
        )
        Box(
            modifier = Modifier
                .weight(weight = 1f)
                .height(height = legendHeight)
                .background(
                    brush = Brush.horizontalGradient(colors = colors),
                    shape = CircleShape
                )
        )
        Text(
            text = endLabel,
            style = VadretTheme.textStyles.label,
            color = VadretTheme.colorScheme.onSurfaceVariant
        )
    }
}