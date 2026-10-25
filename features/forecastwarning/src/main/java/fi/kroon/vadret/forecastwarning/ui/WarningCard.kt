package fi.kroon.vadret.forecastwarning.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fi.kroon.vadret.aggregatedfeed.model.WarningLevel
import fi.kroon.vadret.core.R
import fi.kroon.vadret.forecastwarning.extension.displayName
import fi.kroon.vadret.forecastwarning.model.WarningDetail
import fi.kroon.vadret.forecastwarning.model.WarningItem
import fi.kroon.vadret.ui.VadretCard
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.ui.elapsedTimeText

private val levelChipHeight = 26.dp
private val levelChipStartPadding = 8.dp
private val levelChipEndPadding = 10.dp
private val levelChipSpacing = 6.dp
private val smallIconSize = 16.dp
private val detailLabelWidth = 88.dp
private val detailRowSpacing = 6.dp

@Composable
internal fun WarningCard(
    warning: WarningItem,
    modifier: Modifier = Modifier
) {
    VadretCard(modifier = modifier) {
        Column(modifier = Modifier.padding(all = VadretTheme.spacing.large)) {
            WarningCardHeader(warning = warning)
            Text(
                text = warning.headline,
                style = VadretTheme.textStyles.heading,
                color = VadretTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = VadretTheme.spacing.medium)
            )
            val areaName: String? = warning.areaName
            if (areaName != null) {
                AreaRow(
                    areaName = areaName,
                    modifier = Modifier.padding(top = VadretTheme.spacing.extraSmall)
                )
            }
            if (warning.details.isNotEmpty()) {
                HorizontalDivider(
                    color = VadretTheme.colors.divider,
                    modifier = Modifier.padding(top = VadretTheme.spacing.medium)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(space = detailRowSpacing),
                    modifier = Modifier.padding(top = VadretTheme.spacing.medium)
                ) {
                    warning.details.forEach { warningDetail: WarningDetail ->
                        WarningDetailRow(warningDetail = warningDetail)
                    }
                }
            }
        }
    }
}

@Composable
private fun WarningCardHeader(
    warning: WarningItem,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        val warningLevel: WarningLevel? = warning.warningLevel
        if (warningLevel != null) {
            WarningLevelChip(warningLevel = warningLevel)
        }
        Text(
            text = stringResource(
                id = R.string.source_and_time,
                warning.source.displayName(),
                elapsedTimeText(elapsedTime = warning.elapsedTime)
            ),
            style = VadretTheme.textStyles.label,
            color = VadretTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = VadretTheme.spacing.small)
        )
    }
}

@Composable
private fun WarningLevelChip(
    warningLevel: WarningLevel,
    modifier: Modifier = Modifier
) {
    val levelColors: LevelColors = warningLevel.levelColors()
    Surface(
        shape = CircleShape,
        color = levelColors.container,
        contentColor = levelColors.content,
        modifier = modifier.height(height = levelChipHeight)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(space = levelChipSpacing),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                start = levelChipStartPadding,
                end = levelChipEndPadding
            )
        ) {
            Icon(
                painter = painterResource(id = warningLevel.iconResourceId()),
                contentDescription = null,
                modifier = Modifier.size(size = smallIconSize)
            )
            Text(
                text = stringResource(id = warningLevel.labelResourceId()),
                style = VadretTheme.textStyles.labelStrong
            )
        }
    }
}

@Composable
private fun AreaRow(
    areaName: String,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = VadretTheme.spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_location_on),
            contentDescription = null,
            tint = VadretTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(size = smallIconSize)
        )
        Text(
            text = areaName,
            style = VadretTheme.textStyles.detail,
            color = VadretTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun WarningDetailRow(
    warningDetail: WarningDetail,
    modifier: Modifier = Modifier
) {
    val label: String? = when (warningDetail) {
        is WarningDetail.Labelled -> stringResource(id = warningDetail.labelResourceId)
        is WarningDetail.Titled -> warningDetail.title
        is WarningDetail.Paragraph -> null
    }
    if (label != null) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(space = VadretTheme.spacing.medium),
            modifier = modifier.fillMaxWidth()
        ) {
            Text(
                text = label,
                style = VadretTheme.textStyles.detailLabel,
                color = VadretTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End,
                modifier = Modifier.width(width = detailLabelWidth)
            )
            Text(
                text = warningDetail.text,
                style = VadretTheme.textStyles.detail,
                color = VadretTheme.colorScheme.onSurface,
                modifier = Modifier.weight(weight = 1f)
            )
        }
    } else {
        Text(
            text = warningDetail.text,
            style = VadretTheme.textStyles.detail,
            color = VadretTheme.colorScheme.onSurface,
            modifier = modifier
        )
    }
}