package fi.kroon.vadret.weatherforecast.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.kroon.vadret.core.R
import fi.kroon.vadret.theme.model.Theme
import fi.kroon.vadret.ui.ThemePreviewParameterProvider
import fi.kroon.vadret.ui.VadretCard
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.weatherforecast.model.ComingDay
import kotlinx.collections.immutable.ImmutableList

private const val COLLAPSED_DEGREES = 0f
private const val EXPANDED_DEGREES = 180f
private const val SINGLE_LINE = 1

private val headerInset = 4.dp
private val dayNameWidth = 60.dp
private val dayIconSize = 26.dp
private val dayPrecipitationWidth = 42.dp
private val dayTemperatureWidth = 28.dp
private val chevronSize = 20.dp
private val dividerStartPadding = 16.dp
private val hoursBottomPadding = 14.dp
private val dayNameAutoSize = TextAutoSize.StepBased(
    minFontSize = 11.sp,
    maxFontSize = 15.sp
)

@Composable
internal fun ComingDaysHeader(
    dayCount: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = pluralStringResource(
            id = R.plurals.coming_days_title,
            count = dayCount,
            dayCount
        ),
        style = VadretTheme.textStyles.heading,
        color = VadretTheme.colorScheme.onBackground,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = headerInset
            )
    )
}

@Composable
internal fun ComingDaysCard(
    comingDays: ImmutableList<ComingDay>,
    onDayToggled: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    VadretCard(modifier = modifier) {
        comingDays.forEachIndexed { index: Int, comingDay: ComingDay ->
            if (index > 0) {
                HorizontalDivider(
                    color = VadretTheme.colors.divider,
                    modifier = Modifier.padding(start = dividerStartPadding)
                )
            }
            ComingDayItem(
                comingDay = comingDay,
                onDayToggled = onDayToggled,
                isFirst = index == 0,
                isLast = index == comingDays.lastIndex
            )
        }
    }
}

@Composable
private fun ComingDayItem(
    comingDay: ComingDay,
    onDayToggled: (String) -> Unit,
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ComingDayRow(
            comingDay = comingDay,
            isFirst = isFirst,
            isLast = isLast,
            onClick = { onDayToggled(comingDay.key) }
        )
        AnimatedVisibility(visible = comingDay.isExpanded) {
            ComingHourStrip(
                hours = comingDay.hours,
                firstShownHourIndex = comingDay.firstShownHourIndex,
                modifier = Modifier.padding(bottom = hoursBottomPadding)
            )
        }
    }
}

@Composable
private fun ComingDayRow(
    comingDay: ComingDay,
    onClick: () -> Unit,
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    val degree: String = stringResource(id = R.string.degree)
    val expansionDescription: String = if (comingDay.isExpanded) {
        stringResource(id = R.string.expanded)
    } else {
        stringResource(id = R.string.collapsed)
    }
    val chevronRotation: Float by animateFloatAsState(
        targetValue = if (comingDay.isExpanded) {
            EXPANDED_DEGREES
        } else {
            COLLAPSED_DEGREES
        }
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .clickable(
                interactionSource = null,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .semantics { stateDescription = expansionDescription }
            .padding(
                top = if (isFirst) 16.dp else 16.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = if (isLast) 16.dp else 16.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.width(width = dayNameWidth)
        ) {
            Text(
                text = if (comingDay.isToday) {
                    stringResource(id = R.string.today)
                } else {
                    comingDay.weekdayText
                },
                style = VadretTheme.textStyles.bodyStrong,
                color = VadretTheme.colorScheme.onSurface,
                autoSize = dayNameAutoSize,
                maxLines = SINGLE_LINE
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = comingDay.dateText,
                style = VadretTheme.textStyles.label,
                color = VadretTheme.colorScheme.onSurfaceVariant,
                maxLines = SINGLE_LINE
            )
        }
        Icon(
            painter = painterResource(id = comingDay.iconResourceId),
            contentDescription = stringResource(id = comingDay.descriptionResourceId),
            tint = VadretTheme.colors.icon,
            modifier = Modifier.size(size = dayIconSize)
        )
        Text(
            text = comingDay.precipitationText.withMillimeters(),
            style = VadretTheme.textStyles.label,
            color = VadretTheme.colors.precipitation,
            textAlign = TextAlign.End,
            maxLines = SINGLE_LINE,
            modifier = Modifier.width(width = dayPrecipitationWidth)
        )
        Text(
            text = "${comingDay.lowTemperatureText}$degree",
            style = VadretTheme.textStyles.detail,
            color = VadretTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            maxLines = SINGLE_LINE,
            modifier = Modifier.width(width = dayTemperatureWidth)
        )
        TemperatureRangeBar(
            rangeStart = comingDay.temperatureRangeStart,
            rangeEnd = comingDay.temperatureRangeEnd,
            modifier = Modifier.weight(weight = 1f)
        )
        Text(
            text = "${comingDay.highTemperatureText}$degree",
            style = VadretTheme.textStyles.detailStrong,
            color = VadretTheme.colorScheme.onSurface,
            maxLines = SINGLE_LINE,
            modifier = Modifier.width(width = dayTemperatureWidth)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_expand_more),
            contentDescription = null,
            tint = VadretTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size(size = chevronSize)
                .rotate(degrees = chevronRotation)
        )
    }
}

@Composable
private fun String?.withMillimeters(): String {
    val precipitationText: String = this ?: return ""
    return "$precipitationText ${stringResource(id = R.string.millimeter)}"
}

@Preview
@Composable
private fun ComingDaysCardPreview(
    @PreviewParameter(provider = ThemePreviewParameterProvider::class) theme: Theme
) {
    VadretTheme(theme = theme) {
        ComingDaysCard(
            comingDays = comingDaysPreview(),
            onDayToggled = {},
            modifier = Modifier
                .background(color = VadretTheme.colorScheme.background)
                .padding(all = VadretTheme.spacing.medium)
        )
    }
}