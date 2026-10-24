package fi.kroon.vadret.weatherforecast.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fi.kroon.vadret.core.R
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.weatherforecast.model.WeatherForecastRow

private val temperatureFlairWidth = 6.dp
private val summaryIconSize = 32.dp
private val windArrowSize = 32.dp
private val weatherSymbolSize = 48.dp

@Composable
internal fun HeadlineCard(
    row: WeatherForecastRow.Headline,
    modifier: Modifier = Modifier
) {
    val headline: String = listOf(
        stringResource(id = row.descriptionResourceId),
        stringResource(id = R.string.ws_with),
        stringResource(id = R.string.ws_mostly),
        stringResource(id = row.windSpeedClassResourceId),
        stringResource(id = row.windDirectionResourceId),
        stringResource(id = R.string.ws_wind)
    ).joinToString(separator = " ", postfix = ".")
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(all = VadretTheme.spacing.large)
        ) {
            Text(
                text = headline,
                style = VadretTheme.typography.bodyLarge,
                modifier = Modifier.weight(weight = 1f)
            )
            Image(
                painter = painterResource(id = R.drawable.wsymb2_wind_direction_arrow),
                contentDescription = stringResource(id = row.windDirectionResourceId),
                colorFilter = ColorFilter.tint(color = VadretTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier
                    .padding(start = VadretTheme.spacing.large)
                    .size(size = windArrowSize)
                    .rotate(degrees = row.windArrowRotation)
            )
        }
    }
}

@Composable
internal fun SummaryCard(
    row: WeatherForecastRow.Summary,
    modifier: Modifier = Modifier
) {
    val degree: String = stringResource(id = R.string.degree)
    val feelsLikeTemperature: String? = row.feelsLikeTemperature
    val precipitationResourceId: Int? = row.precipitationResourceId
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(space = VadretTheme.spacing.extraLarge),
            modifier = Modifier.padding(all = VadretTheme.spacing.extraLarge)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SummaryCell(
                    iconResourceId = R.drawable.wsymb2_sun,
                    title = stringResource(id = R.string.sunrise),
                    text = row.sunriseText ?: stringResource(id = R.string.sun_wont_rise_today),
                    modifier = Modifier.weight(weight = 1f)
                )
                Text(
                    text = "${row.temperature}$degree",
                    style = VadretTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(weight = 1f)
                )
                SummaryCell(
                    iconResourceId = R.drawable.wsymb2_moon,
                    title = stringResource(id = R.string.sunset),
                    text = row.sunsetText ?: stringResource(id = R.string.sun_wont_set_today),
                    modifier = Modifier.weight(weight = 1f)
                )
            }
            Row(verticalAlignment = Alignment.Top) {
                SummaryCell(
                    iconResourceId = R.drawable.wsymb2_droplet,
                    text = "${row.humidityPercent}${stringResource(id = R.string.percent)}",
                    modifier = Modifier.weight(weight = 1f)
                )
                SummaryCell(
                    iconResourceId = R.drawable.wsymb2_thermometer,
                    text = if (feelsLikeTemperature != null) {
                        "${stringResource(id = R.string.feels_like)} $feelsLikeTemperature$degree"
                    } else {
                        "${row.temperature}$degree"
                    },
                    title = if (precipitationResourceId != null) {
                        stringResource(id = precipitationResourceId)
                    } else {
                        null
                    },
                    modifier = Modifier.weight(weight = 1f)
                )
                SummaryCell(
                    iconResourceId = R.drawable.wsymb2_wind,
                    text = "${row.windSpeed}${stringResource(id = R.string.m_s)}",
                    modifier = Modifier.weight(weight = 1f)
                )
            }
        }
    }
}

@Composable
private fun SummaryCell(
    iconResourceId: Int,
    text: String,
    modifier: Modifier = Modifier,
    title: String? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = iconResourceId),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = VadretTheme.colorScheme.onSurfaceVariant),
            modifier = Modifier.size(size = summaryIconSize)
        )
        if (title != null) {
            Text(
                text = title,
                style = VadretTheme.typography.bodySmall,
                color = VadretTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = VadretTheme.spacing.extraSmall)
            )
        }
        Text(
            text = text,
            style = VadretTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = VadretTheme.spacing.extraSmall)
        )
    }
}

@Composable
internal fun DayHeader(
    row: WeatherForecastRow.Day,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = VadretTheme.spacing.small,
                end = VadretTheme.spacing.small,
                top = VadretTheme.spacing.extraLarge,
                bottom = VadretTheme.spacing.small
            )
    ) {
        Text(
            text = row.weekday,
            style = VadretTheme.typography.titleLarge,
            color = VadretTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(weight = 1f)
        )
        Text(
            text = row.dateText,
            style = VadretTheme.typography.titleLarge,
            color = VadretTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
internal fun HourCard(
    row: WeatherForecastRow.Hour,
    modifier: Modifier = Modifier
) {
    val degree: String = stringResource(id = R.string.degree)
    val feelsLikeTemperature: String? = row.feelsLikeTemperature
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(width = temperatureFlairWidth)
                    .fillMaxHeight()
                    .background(color = colorResource(id = row.temperatureColorResourceId))
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(weight = 1f)
                    .padding(
                        horizontal = VadretTheme.spacing.large,
                        vertical = VadretTheme.spacing.medium
                    )
            ) {
                Column(modifier = Modifier.weight(weight = 1f)) {
                    Text(
                        text = row.timeText,
                        style = VadretTheme.typography.titleLarge
                    )
                    Text(
                        text = stringResource(id = row.descriptionResourceId),
                        style = VadretTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = VadretTheme.colorScheme.onSurfaceVariant
                    )
                    if (feelsLikeTemperature != null) {
                        Text(
                            text = "${stringResource(id = R.string.feels_like)} $feelsLikeTemperature$degree",
                            style = VadretTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = VadretTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = "${row.temperature}$degree",
                    style = VadretTheme.typography.titleLarge
                )
                Image(
                    painter = painterResource(id = row.iconResourceId),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = VadretTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier
                        .padding(start = VadretTheme.spacing.medium)
                        .size(size = weatherSymbolSize)
                )
            }
        }
    }
}