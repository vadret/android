package fi.kroon.vadret.weatherforecast.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import fi.kroon.vadret.core.resources.Res
import fi.kroon.vadret.core.resources.ic_warning_filled
import fi.kroon.vadret.core.resources.warning_count
import fi.kroon.vadret.theme.model.Theme
import fi.kroon.vadret.ui.ThemePreviewParameterProvider
import fi.kroon.vadret.ui.VadretTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource

private val chipHeight = 28.dp
private val chipStartPadding = 8.dp
private val chipEndPadding = 10.dp
private val chipIconSize = 16.dp
private const val PREVIEW_WARNING_COUNT = 3

@Composable
internal fun WarningCountChip(
    warningCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = VadretTheme.colors.warningYellow,
        contentColor = VadretTheme.colors.onWarningYellow,
        modifier = modifier.height(height = chipHeight)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(space = VadretTheme.spacing.extraSmall),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                start = chipStartPadding,
                end = chipEndPadding
            )
        ) {
            Icon(
                painter = painterResource(resource = Res.drawable.ic_warning_filled),
                contentDescription = null,
                modifier = Modifier.size(size = chipIconSize)
            )
            Text(
                text = pluralStringResource(
                    resource = Res.plurals.warning_count,
                    quantity = warningCount,
                    warningCount
                ),
                style = VadretTheme.textStyles.labelStrong
            )
        }
    }
}

@Preview
@Composable
private fun WarningCountChipPreview(
    @PreviewParameter(provider = ThemePreviewParameterProvider::class) theme: Theme
) {
    VadretTheme(theme = theme) {
        WarningCountChip(
            warningCount = PREVIEW_WARNING_COUNT,
            onClick = {}
        )
    }
}