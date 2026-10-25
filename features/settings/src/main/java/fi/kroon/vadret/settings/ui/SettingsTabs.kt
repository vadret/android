package fi.kroon.vadret.settings.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fi.kroon.vadret.ui.VadretSlidingSelector
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
        VadretSlidingSelector(
            optionCount = SettingsPage.entries.size,
            selectedIndex = selectedPage.ordinal,
            onOptionSelected = { index: Int -> onPageClick(SettingsPage.entries[index]) },
            role = Role.Tab,
            isEqualWidth = true,
            modifier = Modifier
                .fillMaxSize()
                .padding(all = tabsInnerPadding)
        ) { index: Int, contentColor: Color ->
            Text(
                text = stringResource(id = SettingsPage.entries[index].titleResourceId),
                style = VadretTheme.textStyles.bodyStrong,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}