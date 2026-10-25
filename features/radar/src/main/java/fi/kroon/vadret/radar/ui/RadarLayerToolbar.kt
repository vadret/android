package fi.kroon.vadret.radar.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fi.kroon.vadret.core.R
import fi.kroon.vadret.radar.model.RadarLayer
import fi.kroon.vadret.ui.VadretTheme

private val toolbarPadding = PaddingValues(all = 4.dp)
private val toolbarHeight = 44.dp
private val toolbarElevation = 4.dp
private val toggleHeight = 36.dp
private val toggleIconSize = 18.dp
private val togglePadding = PaddingValues(
    start = 10.dp,
    end = 14.dp
)
private val pillShapes = ToggleButtonShapes(
    shape = CircleShape,
    pressedShape = CircleShape,
    checkedShape = CircleShape
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
        RadarLayer.entries.forEach { layer: RadarLayer ->
            LayerToggle(
                layer = layer,
                isSelected = layer == selectedLayer,
                onClick = { onLayerSelected(layer) }
            )
        }
    }
}

@Composable
private fun LayerToggle(
    layer: RadarLayer,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconTint: Color = if (isSelected) {
        VadretTheme.colorScheme.onPrimary
    } else {
        VadretTheme.colorScheme.onSurfaceVariant
    }
    ToggleButton(
        checked = isSelected,
        onCheckedChange = { onClick() },
        shapes = pillShapes,
        colors = ToggleButtonDefaults.colors(
            containerColor = Color.Transparent,
            contentColor = VadretTheme.colorScheme.onSurface,
            checkedContainerColor = VadretTheme.colorScheme.primary,
            checkedContentColor = VadretTheme.colorScheme.onPrimary
        ),
        icon = {
            Icon(
                painter = painterResource(id = layer.iconResourceId(isSelected = isSelected)),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(size = toggleIconSize)
            )
        },
        contentPadding = togglePadding,
        modifier = modifier.height(height = toggleHeight)
    ) {
        Text(
            text = stringResource(id = layer.labelResourceId()),
            style = VadretTheme.textStyles.detailStrong
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