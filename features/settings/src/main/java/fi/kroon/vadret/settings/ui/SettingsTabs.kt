package fi.kroon.vadret.settings.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fi.kroon.vadret.ui.VadretTheme

private val tabsHeight = 44.dp
private val tabsInnerPadding = 4.dp
private val tabsBorderWidth = 1.dp

@Composable
internal fun SettingsTabs(
    selectedPage: SettingsPage,
    onPageClick: (SettingsPage) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = CircleShape,
        color = VadretTheme.colorScheme.surface,
        border = BorderStroke(
            width = tabsBorderWidth,
            color = VadretTheme.colors.cardBorder
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(height = tabsHeight)
    ) {
        Row(
            modifier = Modifier
                .padding(all = tabsInnerPadding)
                .selectableGroup()
        ) {
            SettingsPage.entries.forEach { page: SettingsPage ->
                SettingsTab(
                    page = page,
                    isSelected = page == selectedPage,
                    onClick = { onPageClick(page) },
                    modifier = Modifier.weight(weight = 1f)
                )
            }
        }
    }
}

@Composable
private fun SettingsTab(
    page: SettingsPage,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor: Color by animateColorAsState(
        targetValue = if (isSelected) {
            VadretTheme.colorScheme.primary
        } else {
            Color.Transparent
        },
        label = "tabContainer"
    )
    val contentColor: Color by animateColorAsState(
        targetValue = if (isSelected) {
            VadretTheme.colorScheme.onPrimary
        } else {
            VadretTheme.colorScheme.onSurfaceVariant
        },
        label = "tabContent"
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxHeight()
            .clip(shape = CircleShape)
            .background(color = containerColor)
            .selectable(
                selected = isSelected,
                role = Role.Tab,
                onClick = onClick
            )
    ) {
        Text(
            text = stringResource(id = page.titleResourceId),
            style = VadretTheme.textStyles.bodyStrong,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}