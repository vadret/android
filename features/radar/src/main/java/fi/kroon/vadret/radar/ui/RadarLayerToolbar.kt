package fi.kroon.vadret.radar.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import fi.kroon.vadret.core.R
import fi.kroon.vadret.radar.model.RadarLayer
import fi.kroon.vadret.ui.VadretSlidingSelector
import fi.kroon.vadret.ui.VadretTheme

private val toolbarPadding = PaddingValues(all = 4.dp)
private val toolbarHeight = 44.dp
private val toolbarElevation = 4.dp
private val layerIconSize = 18.dp
private val layerIconGap = 8.dp
private val layerPadding = PaddingValues(
    start = 10.dp,
    end = 14.dp
)

@Composable
internal fun RadarLayerToolbar(
    selectedLayer: RadarLayer,
    onLayerSelected: (RadarLayer) -> Unit,
    modifier: Modifier = Modifier
) {
    HorizontalFloatingToolbar(
        expanded = true,
        colors = FloatingToolbarDefaults.standardFloatingToolbarColors(
            toolbarContainerColor = VadretTheme.colorScheme.surface,
            toolbarContentColor = VadretTheme.colorScheme.onSurface
        ),
        contentPadding = toolbarPadding,
        shape = CircleShape,
        expandedShadowElevation = toolbarElevation,
        modifier = modifier.height(height = toolbarHeight)
    ) {
        VadretSlidingSelector(
            optionCount = RadarLayer.entries.size,
            selectedIndex = selectedLayer.ordinal,
            onOptionSelected = { index: Int -> onLayerSelected(RadarLayer.entries[index]) },
            role = Role.RadioButton,
            modifier = Modifier.fillMaxHeight(),
            unselectedContentColor = VadretTheme.colorScheme.onSurface
        ) { index: Int, contentColor: Color ->
            LayerLabel(
                layer = RadarLayer.entries[index],
                isSelected = index == selectedLayer.ordinal,
                contentColor = contentColor
            )
        }
    }
}

@Composable
private fun LayerLabel(
    layer: RadarLayer,
    isSelected: Boolean,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    val iconTint: Color by animateColorAsState(
        targetValue = if (isSelected) {
            VadretTheme.colorScheme.onPrimaryContainer
        } else {
            VadretTheme.colorScheme.onSurfaceVariant
        },
        label = "layerIcon"
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = layerIconGap),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(paddingValues = layerPadding)
    ) {
        Icon(
            painter = painterResource(id = layer.iconResourceId(isSelected = isSelected)),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(size = layerIconSize)
        )
        Text(
            text = stringResource(id = layer.labelResourceId()),
            style = VadretTheme.textStyles.detailStrong,
            color = contentColor
        )
    }
}

private fun RadarLayer.iconResourceId(isSelected: Boolean): Int =
    when (this) {
        RadarLayer.PRECIPITATION -> if (isSelected) {
            R.drawable.ic_rainy_filled
        } else {
            R.drawable.ic_rainy
        }

        RadarLayer.WIND -> R.drawable.ic_air
    }

private fun RadarLayer.labelResourceId(): Int =
    when (this) {
        RadarLayer.PRECIPITATION -> R.string.precipitation
        RadarLayer.WIND -> R.string.wind
    }