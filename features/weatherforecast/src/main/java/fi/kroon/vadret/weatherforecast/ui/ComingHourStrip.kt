package fi.kroon.vadret.weatherforecast.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import fi.kroon.vadret.core.R
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.weatherforecast.model.ComingHour
import kotlinx.collections.immutable.ImmutableList

private const val HOUR_CONTENT_TYPE = "coming-hour"
private const val SINGLE_LINE = 1

private val stripStartPadding = 10.dp
private val stripBottomPadding = 8.dp
private val stripPadding = PaddingValues(
    start = stripStartPadding,
    end = 10.dp,
    top = 2.dp,
    bottom = stripBottomPadding
)
private val hourColumnWidth = 46.dp
private val hourCellTopPadding = 8.dp
private val hourCellContentSpacing = 5.dp
private val hourIconSize = 22.dp
private val temperatureLegendSpacing = 6.dp
private val temperatureChartHeight = 64.dp
private val temperatureLineTop = 20.dp
private val temperatureLineRange = 36.dp
private val windLegendSpacing = 8.dp
private val windChartHeight = 52.dp
private val windLineTop = 22.dp
private val windLineRange = 26.dp
private val legendHeight = 18.dp
private val legendHorizontalPadding = 4.dp
private val legendSpacing = 6.dp
private val legendSwatchWidth = 12.dp
private val legendSwatchHeight = 3.dp
private val legendSwatchShape = RoundedCornerShape(size = 2.dp)

@Composable
internal fun ComingHourStrip(
    hours: ImmutableList<ComingHour>,
    firstShownHourIndex: Int,
    modifier: Modifier = Modifier
) {
    val textMeasurer: TextMeasurer = rememberTextMeasurer()
    val temperatureColor: Color = VadretTheme.colors.temperatureWarm
    val windColor: Color = VadretTheme.colorScheme.primary
    Box(modifier = modifier.fillMaxWidth()) {
        LazyRow(
            state = rememberLazyListState(initialFirstVisibleItemIndex = firstShownHourIndex),
            contentPadding = stripPadding,
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(
                items = hours,
                key = { _: Int, comingHour: ComingHour -> comingHour.key },
                contentType = { _: Int, _: ComingHour -> HOUR_CONTENT_TYPE }
            ) { index: Int, _: ComingHour ->
                ComingHourColumn(
                    hours = hours,
                    index = index,
                    textMeasurer = textMeasurer,
                    temperatureColor = temperatureColor,
                    windColor = windColor
                )
            }
        }
        ChartLegends(
            temperatureColor = temperatureColor,
            windColor = windColor,
            modifier = Modifier
                .align(alignment = Alignment.BottomStart)
                .padding(
                    start = stripStartPadding,
                    bottom = stripBottomPadding
                )
        )
    }
}

@Composable
private fun ComingHourColumn(
    hours: ImmutableList<ComingHour>,
    index: Int,
    textMeasurer: TextMeasurer,
    temperatureColor: Color,
    windColor: Color,
    modifier: Modifier = Modifier
) {
    val comingHour: ComingHour = hours[index]
    val temperatureText = "${comingHour.temperatureText}${stringResource(id = R.string.degree)}"
    val windDescription = "${stringResource(id = comingHour.windDirectionResourceId)} ${comingHour.windSpeedText} ${stringResource(id = R.string.m_s)}"
    Column(
        modifier = modifier
            .width(width = hourColumnWidth)
            .semantics(mergeDescendants = true) {}
    ) {
        ComingHourCell(comingHour = comingHour)
        HourChartSlice(
            hours = hours,
            index = index,
            level = { shownHour: ComingHour -> shownHour.temperatureLevel },
            lineColor = temperatureColor,
            lineTop = temperatureLineTop,
            lineRange = temperatureLineRange,
            labelText = temperatureText,
            labelStyle = VadretTheme.textStyles.detailLabelStrong,
            labelColor = VadretTheme.colorScheme.onSurface,
            textMeasurer = textMeasurer,
            modifier = Modifier
                .padding(top = temperatureLegendSpacing + legendHeight)
                .height(height = temperatureChartHeight)
                .semantics { contentDescription = temperatureText }
        )
        HourChartSlice(
            hours = hours,
            index = index,
            level = { shownHour: ComingHour -> shownHour.windLevel },
            lineColor = windColor,
            lineTop = windLineTop,
            lineRange = windLineRange,
            labelText = comingHour.windSpeedText,
            labelStyle = VadretTheme.textStyles.label,
            labelColor = VadretTheme.colorScheme.onSurfaceVariant,
            textMeasurer = textMeasurer,
            arrow = painterResource(id = R.drawable.ic_north),
            arrowRotation = comingHour.windArrowRotation,
            modifier = Modifier
                .padding(top = windLegendSpacing + legendHeight)
                .height(height = windChartHeight)
                .semantics { contentDescription = windDescription }
        )
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
            .fillMaxWidth()
            .padding(top = hourCellTopPadding)
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
            text = comingHour.precipitationText.orEmpty(),
            style = VadretTheme.textStyles.label,
            color = VadretTheme.colors.precipitation,
            maxLines = SINGLE_LINE
        )
    }
}

@Composable
private fun ChartLegends(
    temperatureColor: Color,
    windColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        ChartLegend(
            title = stringResource(id = R.string.temperature),
            color = temperatureColor
        )
        Spacer(modifier = Modifier.height(height = temperatureChartHeight + windLegendSpacing))
        ChartLegend(
            title = stringResource(id = R.string.wind_in_meters_per_second),
            color = windColor
        )
        Spacer(modifier = Modifier.height(height = windChartHeight))
    }
}

@Composable
private fun ChartLegend(
    title: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = legendSpacing),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(height = legendHeight)
            .padding(horizontal = legendHorizontalPadding)
    ) {
        Box(
            modifier = Modifier
                .size(
                    width = legendSwatchWidth,
                    height = legendSwatchHeight
                )
                .background(
                    color = color,
                    shape = legendSwatchShape
                )
        )
        Text(
            text = title,
            style = VadretTheme.textStyles.label,
            color = VadretTheme.colorScheme.onSurfaceVariant,
            maxLines = SINGLE_LINE
        )
    }
}