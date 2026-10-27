package fi.kroon.vadret.weatherforecast.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fi.kroon.vadret.core.R
import fi.kroon.vadret.icons.VadretIcon
import fi.kroon.vadret.ui.VadretCard
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.weatherforecast.model.ForecastDay
import fi.kroon.vadret.weatherforecast.model.ForecastHour

private val dayHeaderTopPadding = 6.dp
private val dayHeaderInset = 4.dp
private val hourRowHeight = 58.dp
private val hourRowSpacing = 14.dp
private val hourTimeWidth = 44.dp
private val hourIconSize = 24.dp
private val temperatureSpacing = 1.dp
private const val DESCRIPTION_MAX_LINES = 2

@Composable
internal fun ForecastDayHeader(
    forecastDay: ForecastDay,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = dayHeaderInset,
                end = dayHeaderInset,
                top = dayHeaderTopPadding
            )
    ) {
        Text(
            text = forecastDay.weekday,
            style = VadretTheme.textStyles.heading,
            color = VadretTheme.colorScheme.onBackground,
            modifier = Modifier
                .weight(weight = 1f)
                .alignByBaseline()
        )
        Text(
            text = forecastDay.dateText,
            style = VadretTheme.textStyles.detail,
            color = VadretTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.alignByBaseline()
        )
    }
}

@Composable
internal fun ForecastDayCard(
    forecastDay: ForecastDay,
    modifier: Modifier = Modifier
) {
    VadretCard(modifier = modifier) {
        forecastDay.hours.forEachIndexed { index: Int, forecastHour: ForecastHour ->
            if (index > 0) {
                HorizontalDivider(color = VadretTheme.colors.divider)
            }
            ForecastHourRow(forecastHour = forecastHour)
        }
    }
}

@Composable
private fun ForecastHourRow(
    forecastHour: ForecastHour,
    modifier: Modifier = Modifier
) {
    val degree: String = stringResource(id = R.string.degree)
    val feelsLikeTemperature: String? = forecastHour.feelsLikeTemperature
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = hourRowSpacing),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(height = hourRowHeight)
            .padding(horizontal = VadretTheme.spacing.large)
    ) {
        Text(
            text = forecastHour.timeText,
            style = VadretTheme.textStyles.bodyStrong,
            color = VadretTheme.colorScheme.onSurface,
            modifier = Modifier.width(width = hourTimeWidth)
        )
        VadretIcon(
            icon = forecastHour.icon,
            contentDescription = null,
            tint = VadretTheme.colors.icon,
            modifier = Modifier.size(size = hourIconSize)
        )
        Text(
            text = stringResource(id = forecastHour.descriptionResourceId),
            style = VadretTheme.textStyles.body,
            color = VadretTheme.colorScheme.onSurface,
            maxLines = DESCRIPTION_MAX_LINES,
            modifier = Modifier.weight(weight = 1f)
        )
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(space = temperatureSpacing)
        ) {
            Text(
                text = "${forecastHour.temperature}$degree",
                style = VadretTheme.textStyles.value,
                color = VadretTheme.colorScheme.onSurface
            )
            if (feelsLikeTemperature != null) {
                Text(
                    text = "${stringResource(id = R.string.feels_like)} $feelsLikeTemperature$degree",
                    style = VadretTheme.textStyles.label,
                    color = VadretTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}