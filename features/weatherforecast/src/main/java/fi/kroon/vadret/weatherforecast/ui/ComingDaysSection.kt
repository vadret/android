package fi.kroon.vadret.weatherforecast.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.kroon.vadret.core.R
import fi.kroon.vadret.ui.VadretCard
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.weatherforecast.model.ComingDay
import fi.kroon.vadret.weatherforecast.model.ComingHour
import fi.kroon.vadret.weatherforecast.model.SunTimes
import kotlinx.collections.immutable.ImmutableList

private const val HOUR_CONTENT_TYPE = "coming-hour"
private const val COLLAPSED_DEGREES = 0f
private const val EXPANDED_DEGREES = 180f
private const val SINGLE_LINE = 1

private val headerInset = 4.dp
private val dayRowMinHeight = 58.dp
private val dayRowStartPadding = 16.dp
private val dayRowEndPadding = 10.dp
private val dayRowSpacing = 8.dp
private val dayNameWidth = 60.dp
private val dayIconSize = 26.dp
private val dayPrecipitationWidth = 42.dp
private val dayTemperatureWidth = 28.dp
private val chevronSize = 20.dp
private val dividerStartPadding = 16.dp
private val hoursBottomPadding = 14.dp
private val hourStripPadding = PaddingValues(
    start = 10.dp,
    end = 10.dp,
    top = 2.dp,
    bottom = 8.dp
)
private val hourCellSpacing = 2.dp
private val hourCellWidth = 46.dp
private val hourCellVerticalPadding = 8.dp
private val hourCellContentSpacing = 6.dp
private val hourIconSize = 22.dp
private val detailsHorizontalPadding = 16.dp
private val detailsHorizontalSpacing = 16.dp
private val detailsVerticalSpacing = 6.dp
private val detailIconSize = 16.dp
private val detailIconSpacing = 4.dp
private val dayNameAutoSize = TextAutoSize.StepBased(
    minFontSize = 11.sp,
    maxFontSize = 15.sp
)

@Composable
internal fun ComingDaysHeader(
    dayCount: Int,
    isEveryDayExpanded: Boolean,
    onAllDaysToggled: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = headerInset)
    ) {
        Text(
            text = pluralStringResource(
                id = R.plurals.coming_days_title,
                count = dayCount,
                dayCount
            ),
            style = VadretTheme.textStyles.heading,
            color = VadretTheme.colorScheme.onBackground,
            modifier = Modifier.weight(weight = 1f)
        )
        TextButton(onClick = onAllDaysToggled) {
            Text(
                text = if (isEveryDayExpanded) {
                    stringResource(id = R.string.collapse_all)
                } else {
                    stringResource(id = R.string.expand_all)
                },
                style = VadretTheme.textStyles.detailStrong
            )
        }
    }
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
                onDayToggled = onDayToggled
            )
        }
    }
}

@Composable
private fun ComingDayItem(
    comingDay: ComingDay,
    onDayToggled: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ComingDayRow(
            comingDay = comingDay,
            onClick = { onDayToggled(comingDay.key) }
        )
        AnimatedVisibility(visible = comingDay.isExpanded) {
            Column(modifier = Modifier.padding(bottom = hoursBottomPadding)) {
                ComingHourStrip(hours = comingDay.hours)
                ComingDayDetails(comingDay = comingDay)
            }
        }
    }
}

@Composable
private fun ComingDayRow(
    comingDay: ComingDay,
    onClick: () -> Unit,
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
        horizontalArrangement = Arrangement.spacedBy(space = dayRowSpacing),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = dayRowMinHeight)
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
            .semantics { stateDescription = expansionDescription }
            .padding(
                start = dayRowStartPadding,
                end = dayRowEndPadding
            )
    ) {
        Column(modifier = Modifier.width(width = dayNameWidth)) {
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
private fun ComingHourStrip(
    hours: ImmutableList<ComingHour>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        contentPadding = hourStripPadding,
        horizontalArrangement = Arrangement.spacedBy(space = hourCellSpacing),
        modifier = modifier.fillMaxWidth()
    ) {
        items(
            items = hours,
            key = { comingHour: ComingHour -> comingHour.key },
            contentType = { HOUR_CONTENT_TYPE }
        ) { comingHour: ComingHour ->
            ComingHourCell(comingHour = comingHour)
        }
    }
}

@Composable
private fun ComingHourCell(
    comingHour: ComingHour,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = hourCellContentSpacing),
        modifier = modifier
            .width(width = hourCellWidth)
            .padding(vertical = hourCellVerticalPadding)
    ) {
        Text(
            text = if (comingHour.isNow) {
                stringResource(id = R.string.now)
            } else {
                comingHour.timeText
            },
            style = VadretTheme.textStyles.label,
            color = VadretTheme.colorScheme.onSurfaceVariant,
            maxLines = SINGLE_LINE
        )
        Icon(
            painter = painterResource(id = comingHour.iconResourceId),
            contentDescription = stringResource(id = comingHour.descriptionResourceId),
            tint = VadretTheme.colors.icon,
            modifier = Modifier.size(size = hourIconSize)
        )
        Text(
            text = "${comingHour.temperatureText}${stringResource(id = R.string.degree)}",
            style = VadretTheme.textStyles.detailStrong,
            color = VadretTheme.colorScheme.onSurface,
            maxLines = SINGLE_LINE
        )
        Text(
            text = comingHour.precipitationText.orEmpty(),
            style = VadretTheme.textStyles.label,
            color = VadretTheme.colors.precipitation,
            maxLines = SINGLE_LINE
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ComingDayDetails(
    comingDay: ComingDay,
    modifier: Modifier = Modifier
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(space = detailsHorizontalSpacing),
        verticalArrangement = Arrangement.spacedBy(space = detailsVerticalSpacing),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = detailsHorizontalPadding)
    ) {
        DayDetail(
            iconResourceId = R.drawable.ic_air,
            iconDescription = stringResource(id = R.string.wind),
            text = "${comingDay.windSpeedText} ${stringResource(id = R.string.m_s)}"
        )
        when (val sunTimes: SunTimes = comingDay.sunTimes) {
            is SunTimes.RiseAndSet -> {
                DayDetail(
                    iconResourceId = R.drawable.ic_wb_twilight,
                    iconDescription = stringResource(id = R.string.sunrise),
                    text = sunTimes.sunriseText
                )
                DayDetail(
                    iconResourceId = R.drawable.ic_nights_stay,
                    iconDescription = stringResource(id = R.string.sunset),
                    text = sunTimes.sunsetText
                )
            }

            SunTimes.PolarNight -> DayDetail(
                iconResourceId = R.drawable.ic_nights_stay,
                iconDescription = null,
                text = stringResource(id = R.string.polar_night)
            )

            SunTimes.MidnightSun -> DayDetail(
                iconResourceId = R.drawable.ic_clear_day,
                iconDescription = null,
                text = stringResource(id = R.string.midnight_sun)
            )
        }
    }
}

@Composable
private fun DayDetail(
    @DrawableRes iconResourceId: Int,
    iconDescription: String?,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = detailIconSpacing),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = iconResourceId),
            contentDescription = iconDescription,
            tint = VadretTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(size = detailIconSize)
        )
        Text(
            text = text,
            style = VadretTheme.textStyles.detailLabel,
            color = VadretTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun String?.withMillimeters(): String {
    val precipitationText: String = this ?: return ""
    return "$precipitationText ${stringResource(id = R.string.millimeter)}"
}