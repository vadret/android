package fi.kroon.vadret.main.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import fi.kroon.vadret.ui.VadretTheme

private val navigationBarHeight = 72.dp
private val indicatorWidth = 56.dp
private val indicatorHeight = 30.dp
private val navigationIconSize = 22.dp

@Composable
internal fun VadretNavigationBar(
    currentKey: NavKey?,
    onDestinationClick: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = VadretTheme.colorScheme.surfaceContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.windowInsetsPadding(insets = WindowInsets.navigationBars)) {
            HorizontalDivider(color = VadretTheme.colorScheme.outlineVariant)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = navigationBarHeight)
                    .selectableGroup()
            ) {
                TopLevelDestination.entries.forEach { destination: TopLevelDestination ->
                    NavigationItem(
                        destination = destination,
                        isSelected = destination.key == currentKey,
                        onClick = { onDestinationClick(destination) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.NavigationItem(
    destination: TopLevelDestination,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navigationItemColors: NavigationItemColors = navigationItemColors(isSelected = isSelected)
    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = VadretTheme.spacing.extraSmall,
            alignment = Alignment.CenterVertically
        ),
        modifier = modifier
            .weight(weight = 1f)
            .fillMaxHeight()
            .selectable(
                selected = isSelected,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onClick
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(
                    width = indicatorWidth,
                    height = indicatorHeight
                ).clip(shape = CircleShape)
                .background(color = navigationItemColors.indicator)
                .indication(
                    interactionSource = interactionSource,
                    indication = ripple()
                )
        ) {
            Icon(
                painter = painterResource(id = destination.iconResourceId(isSelected = isSelected)),
                contentDescription = null,
                tint = navigationItemColors.icon,
                modifier = Modifier.size(size = navigationIconSize)
            )
        }
        Text(
            text = stringResource(id = destination.labelResourceId),
            style = navigationItemColors.labelStyle,
            color = navigationItemColors.label
        )
    }
}

private class NavigationItemColors(
    val indicator: Color,
    val icon: Color,
    val label: Color,
    val labelStyle: TextStyle
)

@Composable
private fun navigationItemColors(isSelected: Boolean): NavigationItemColors =
    if (isSelected) {
        NavigationItemColors(
            indicator = VadretTheme.colorScheme.secondaryContainer,
            icon = VadretTheme.colorScheme.onSecondaryContainer,
            label = VadretTheme.colorScheme.onSurface,
            labelStyle = VadretTheme.textStyles.labelStrong
        )
    } else {
        NavigationItemColors(
            indicator = Color.Transparent,
            icon = VadretTheme.colorScheme.onSurfaceVariant,
            label = VadretTheme.colorScheme.onSurfaceVariant,
            labelStyle = VadretTheme.textStyles.label
        )
    }

private fun TopLevelDestination.iconResourceId(isSelected: Boolean): Int =
    if (isSelected) {
        selectedIconResourceId
    } else {
        iconResourceId
    }