package fi.kroon.vadret.weatherforecast.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import fi.kroon.vadret.core.R
import fi.kroon.vadret.theme.model.Theme
import fi.kroon.vadret.ui.ThemePreviewParameterProvider
import fi.kroon.vadret.ui.VadretCard
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.weatherforecast.model.CurrentWeather

private val weatherIconSize = 64.dp
private val windArrowSize = 15.dp
private val descriptionTopPadding = 6.dp

@Composable
internal fun CurrentWeatherCard(
    currentWeather: CurrentWeather,
    modifier: Modifier = Modifier
) {
    val verticalPadding = 24.dp
    val degree: String = stringResource(id = R.string.degree)
    VadretCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = verticalPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "${currentWeather.temperatureText}$degree",
                        style = VadretTheme.textStyles.temperature,
                        color = VadretTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(id = currentWeather.descriptionResourceId),
                        style = VadretTheme.textStyles.detail,
                        color = VadretTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = descriptionTopPadding)
                    )
                }
                Icon(
                    painter = painterResource(id = currentWeather.iconResourceId),
                    contentDescription = stringResource(id = currentWeather.descriptionResourceId),
                    tint = VadretTheme.colors.icon,
                    modifier = Modifier.size(size = weatherIconSize)
                )
            }
            HorizontalDivider(color = VadretTheme.colors.divider)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = verticalPadding),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Stat(
                    label = stringResource(id = R.string.feels_like),
                    value = "${currentWeather.feelsLikeTemperatureText}$degree"
                )
                Stat(
                    label = stringResource(id = R.string.wind),
                    value = "${currentWeather.windSpeed} ${stringResource(id = R.string.m_s)}",
                    trailing = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_north),
                            contentDescription = stringResource(id = currentWeather.windDirectionResourceId),
                            tint = VadretTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(size = windArrowSize)
                                .rotate(degrees = currentWeather.windArrowRotation)
                        )
                    }
                )
                Stat(
                    label = stringResource(id = R.string.humidity),
                    value = "${currentWeather.humidityPercent}${stringResource(id = R.string.percent)}"
                )
            }
        }
    }
}

@Composable
private fun Stat(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    trailing: @Composable () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 2.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            style = VadretTheme.textStyles.label,
            color = VadretTheme.colorScheme.onSurfaceVariant
        )
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

@Preview
@Composable
private fun CurrentWeatherCardPreview(
    @PreviewParameter(provider = ThemePreviewParameterProvider::class) theme: Theme
) {
    VadretTheme(theme = theme) {
        CurrentWeatherCard(
            currentWeather = currentWeatherPreview(),
            modifier = Modifier
                .background(color = Color.DarkGray)
                .padding(all = VadretTheme.spacing.medium)
        )
    }
}