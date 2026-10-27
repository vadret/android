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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import fi.kroon.vadret.core.resources.Res
import fi.kroon.vadret.core.resources.degree
import fi.kroon.vadret.core.resources.feels_like
import fi.kroon.vadret.core.resources.humidity
import fi.kroon.vadret.core.resources.ic_north
import fi.kroon.vadret.core.resources.m_s
import fi.kroon.vadret.core.resources.percent
import fi.kroon.vadret.core.resources.wind
import fi.kroon.vadret.icons.VadretIcon
import fi.kroon.vadret.theme.model.Theme
import fi.kroon.vadret.ui.ThemePreviewParameterProvider
import fi.kroon.vadret.ui.VadretCard
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.weatherforecast.model.CurrentWeather
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val weatherIconSize = 64.dp
private val windArrowSize = 15.dp
private val descriptionTopPadding = 6.dp

@Composable
internal fun CurrentWeatherCard(
    currentWeather: CurrentWeather,
    modifier: Modifier = Modifier
) {
    val verticalPadding = 24.dp
    val degree: String = stringResource(resource = Res.string.degree)
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
                        text = stringResource(resource = currentWeather.descriptionResource),
                        style = VadretTheme.textStyles.detail,
                        color = VadretTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = descriptionTopPadding)
                    )
                }
                VadretIcon(
                    icon = currentWeather.icon,
                    contentDescription = stringResource(resource = currentWeather.descriptionResource),
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
                    label = stringResource(resource = Res.string.feels_like),
                    value = "${currentWeather.feelsLikeTemperatureText}$degree"
                )
                Stat(
                    label = stringResource(resource = Res.string.wind),
                    value = "${currentWeather.windSpeed} ${stringResource(resource = Res.string.m_s)}",
                    trailing = {
                        Icon(
                            painter = painterResource(resource = Res.drawable.ic_north),
                            contentDescription = stringResource(resource = currentWeather.windDirectionResource),
                            tint = VadretTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(size = windArrowSize)
                                .rotate(degrees = currentWeather.windArrowRotation)
                        )
                    }
                )
                Stat(
                    label = stringResource(resource = Res.string.humidity),
                    value = "${currentWeather.humidityPercent}${stringResource(resource = Res.string.percent)}"
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