package fi.kroon.vadret.forecastwarning.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.core.R
import fi.kroon.vadret.forecastwarning.extension.displayName
import fi.kroon.vadret.forecastwarning.model.ElapsedTime
import fi.kroon.vadret.forecastwarning.model.WarningItem
import fi.kroon.vadret.ui.VadretTheme

private val chipStrokeWidth = 1.dp

@Composable
internal fun WarningCard(
    warning: WarningItem,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(all = VadretTheme.spacing.large)) {
            Text(
                text = warning.headline,
                style = VadretTheme.typography.titleLarge
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = VadretTheme.spacing.small)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_error_outline_black_24dp),
                    contentDescription = null,
                    tint = VadretTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = elapsedTimeText(elapsedTime = warning.elapsedTime),
                    style = VadretTheme.typography.bodyLarge,
                    color = VadretTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = VadretTheme.spacing.small)
                )
            }
            Text(
                text = warning.bodyText,
                style = VadretTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = VadretTheme.spacing.small)
            )
            if (warning.preamble.isNotBlank()) {
                Text(
                    text = warning.preamble,
                    style = VadretTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = VadretTheme.spacing.medium)
                )
            }
            SourceChip(
                source = warning.source,
                modifier = Modifier.padding(top = VadretTheme.spacing.medium)
            )
        }
    }
}

@Composable
private fun SourceChip(
    source: FeedSource,
    modifier: Modifier = Modifier
) {
    val chipColors: ChipColors = source.chipColors()
    Surface(
        shape = CircleShape,
        color = chipColors.container,
        contentColor = VadretTheme.colors.chipText,
        border = BorderStroke(
            width = chipStrokeWidth,
            color = chipColors.stroke
        ),
        modifier = modifier
    ) {
        Text(
            text = source.displayName(),
            style = VadretTheme.typography.labelLarge,
            modifier = Modifier.padding(
                horizontal = VadretTheme.spacing.large,
                vertical = VadretTheme.spacing.small
            )
        )
    }
}

@Composable
private fun elapsedTimeText(elapsedTime: ElapsedTime): String {
    val text: String = stringResource(id = elapsedTime.resourceId)
    val count: Long? = elapsedTime.count
    return if (count != null) {
        "$count $text"
    } else {
        text
    }
}