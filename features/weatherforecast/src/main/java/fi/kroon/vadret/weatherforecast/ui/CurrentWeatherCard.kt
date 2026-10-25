package fi.kroon.vadret.weatherforecast.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fi.kroon.vadret.core.R
import fi.kroon.vadret.ui.VadretCard
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.weatherforecast.model.CurrentWeather

private val weatherIconSize = 56.dp
private val statIconSize = 16.dp
private val cardTopPadding = 20.dp
private val contentInset = 4.dp
private val descriptionTopPadding = 10.dp
private val dividerTopPadding = 16.dp
private val dividerBottomPadding = 14.dp
private val statRowSpacing = 14.dp
private val statLabelSpacing = 2.dp

@Composable
internal fun CurrentWeatherCard(
    currentWeather: CurrentWeather,
    modifier: Modifier = Modifier
) {
    val degree: String = stringResource(id = R.string.degree)
    VadretCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(
                start = VadretTheme.spacing.large,
                end = VadretTheme.spacing.large,
                top = cardTopPadding,
                bottom = VadretTheme.spacing.large
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = contentInset)
            ) {
                Text(
                    text = "${currentWeather.temperature}$degree",
                    style = VadretTheme.textStyles.temperature,
                    color = VadretTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(weight = 1f)
                )
                Icon(
                    painter = painterResource(id = currentWeather.iconResourceId),
                    contentDescription = stringResource(id = currentWeather.descriptionResourceId),
                    tint = VadretTheme.colors.icon,
                    modifier = Modifier.size(size = weatherIconSize)
                )
            }
            Text(
                text = weatherDescription(currentWeather = currentWeather),
                style = VadretTheme.textStyles.body,
                color = VadretTheme.colorScheme.onSurface,
                modifier = Modifier.padding(
                    start = contentInset,
                    end = contentInset,
                    top = descriptionTopPadding
                )
            )
            HorizontalDivider(
                color = VadretTheme.colors.divider,
                modifier = Modifier.padding(
                    top = dividerTopPadding,
                    bottom = dividerBottomPadding
                )
            )
            CurrentWeatherStats(
                currentWeather = currentWeather,
                modifier = Modifier.padding(horizontal = contentInset)
            )
        }
    }
}

@Composable
private fun weatherDescription(currentWeather: CurrentWeather): String {
    val description: String = stringResource(id = currentWeather.descriptionResourceId)
    val withText: String = stringResource(id = R.string.ws_with)
    val mostlyText: String = stringResource(id = R.string.ws_mostly)
    val windSpeedClass: String = stringResource(id = currentWeather.windSpeedClassResourceId)
    val windDirection: String = stringResource(id = currentWeather.windDirectionResourceId)
    val windText: String = stringResource(id = R.string.ws_wind)
    return "$description $withText $mostlyText $windSpeedClass $windDirection $windText."
}

@Composable
private fun CurrentWeatherStats(
    currentWeather: CurrentWeather,
    modifier: Modifier = Modifier
) {
    val degree: String = stringResource(id = R.string.degree)
    Column(
        verticalArrangement = Arrangement.spacedBy(space = statRowSpacing),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(space = VadretTheme.spacing.small)) {
            Stat(
                iconResourceId = R.drawable.ic_device_thermostat,
                label = stringResource(id = R.string.feels_like),
                value = "${currentWeather.feelsLikeTemperature ?: currentWeather.temperature}$degree"
            )
            Stat(
                iconResourceId = R.drawable.ic_air,
                label = stringResource(id = R.string.wind),
                value = "${currentWeather.windSpeed} ${stringResource(id = R.string.m_s)}",
                trailing = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_north),
                        contentDescription = stringResource(id = currentWeather.windDirectionResourceId),
                        tint = VadretTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(size = statIconSize)
                            .rotate(degrees = currentWeather.windArrowRotation)
                    )
                }
            )
            Stat(
                iconResourceId = R.drawable.ic_humidity_percentage,
                label = stringResource(id = R.string.humidity),
                value = "${currentWeather.humidityPercent}${stringResource(id = R.string.percent)}"
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(space = VadretTheme.spacing.small)) {
            Stat(
                iconResourceId = R.drawable.ic_rainy,
                label = stringResource(id = R.string.precipitation_short),
                value = stringResource(id = currentWeather.precipitationResourceId)
            )
            Stat(
                iconResourceId = R.drawable.ic_wb_twilight,
                label = stringResource(id = R.string.sunrise),
                value = currentWeather.sunriseText ?: stringResource(id = R.string.sun_wont_rise_today)
            )
            Stat(
                iconResourceId = R.drawable.ic_nights_stay,
                label = stringResource(id = R.string.sunset),
                value = currentWeather.sunsetText ?: stringResource(id = R.string.sun_wont_set_today)
            )
        }
    }
}

@Composable
private fun RowScope.Stat(
    iconResourceId: Int,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    trailing: @Composable () -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = statLabelSpacing),
        modifier = modifier.weight(weight = 1f)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(space = VadretTheme.spacing.extraSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconResourceId),
                contentDescription = null,
                tint = VadretTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(size = statIconSize)
            )
            Text(
                text = label,
                style = VadretTheme.textStyles.label,
                color = VadretTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(space = VadretTheme.spacing.extraSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                style = VadretTheme.textStyles.value,
                color = VadretTheme.colorScheme.onSurface
            )
            trailing()
        }
    }
}