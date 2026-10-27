package fi.kroon.vadret.weatherforecast.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fi.kroon.vadret.core.resources.Res
import fi.kroon.vadret.core.resources.add_place
import fi.kroon.vadret.core.resources.automatic_location
import fi.kroon.vadret.core.resources.choose_location
import fi.kroon.vadret.core.resources.ic_add
import fi.kroon.vadret.core.resources.ic_check
import fi.kroon.vadret.core.resources.ic_close
import fi.kroon.vadret.core.resources.ic_expand_more
import fi.kroon.vadret.core.resources.ic_location_on_filled
import fi.kroon.vadret.core.resources.ic_my_location
import fi.kroon.vadret.core.resources.remove_favorite
import fi.kroon.vadret.core.resources.selected
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.weatherforecast.WeatherForecastEvent
import fi.kroon.vadret.weatherforecast.model.FavoriteChoice
import fi.kroon.vadret.weatherforecast.model.LocationChoices
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val chipHeight = 36.dp
private val chipBorderWidth = 1.dp
private val chipIconSize = 18.dp
private val chipTextMaxWidth = 180.dp
private val menuIconSize = 20.dp
private val menuShape = RoundedCornerShape(size = 16.dp)
private val menuMinWidth = 220.dp

@Composable
internal fun LocationChip(
    localityName: String?,
    locationChoices: LocationChoices,
    onEvent: (WeatherForecastEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded: Boolean by remember { mutableStateOf(value = false) }
    Box(modifier = modifier) {
        Surface(
            onClick = { isExpanded = true },
            shape = CircleShape,
            color = VadretTheme.colorScheme.surface,
            border = BorderStroke(
                width = chipBorderWidth,
                color = VadretTheme.colorScheme.outline
            ),
            modifier = Modifier.height(height = chipHeight)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(space = VadretTheme.spacing.extraSmall),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = VadretTheme.spacing.small)
            ) {
                Icon(
                    painter = painterResource(resource = Res.drawable.ic_location_on_filled),
                    contentDescription = null,
                    tint = VadretTheme.colorScheme.primary,
                    modifier = Modifier.size(size = chipIconSize)
                )
                Text(
                    text = localityName ?: stringResource(resource = Res.string.automatic_location),
                    style = VadretTheme.textStyles.detailStrong,
                    color = VadretTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = chipTextMaxWidth)
                )
                Icon(
                    painter = painterResource(resource = Res.drawable.ic_expand_more),
                    contentDescription = stringResource(resource = Res.string.choose_location),
                    tint = VadretTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(size = chipIconSize)
                )
            }
        }
        LocationMenu(
            isExpanded = isExpanded,
            locationChoices = locationChoices,
            onDismiss = { isExpanded = false },
            onEvent = { event: WeatherForecastEvent ->
                if (event !is WeatherForecastEvent.OnFavoriteRemoveClicked) {
                    isExpanded = false
                }
                onEvent(event)
            }
        )
    }
}

@Composable
private fun LocationMenu(
    isExpanded: Boolean,
    locationChoices: LocationChoices,
    onDismiss: () -> Unit,
    onEvent: (WeatherForecastEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismiss,
        shape = menuShape,
        containerColor = VadretTheme.colorScheme.surface,
        modifier = modifier.widthIn(min = menuMinWidth)
    ) {
        DropdownMenuItem(
            text = { MenuText(text = stringResource(resource = Res.string.automatic_location)) },
            leadingIcon = { SelectionMark(isSelected = locationChoices.isAutomatic) },
            trailingIcon = { MenuIcon(iconResource = Res.drawable.ic_my_location) },
            onClick = { onEvent(WeatherForecastEvent.OnAutomaticLocationClicked) }
        )
        if (locationChoices.favorites.isNotEmpty()) {
            HorizontalDivider(color = VadretTheme.colors.divider)
        }
        locationChoices.favorites.forEach { favoriteChoice: FavoriteChoice ->
            DropdownMenuItem(
                text = { MenuText(text = favoriteChoice.favorite.locality) },
                leadingIcon = { SelectionMark(isSelected = favoriteChoice.isSelected) },
                trailingIcon = {
                    IconButton(onClick = { onEvent(WeatherForecastEvent.OnFavoriteRemoveClicked(favoriteId = favoriteChoice.favorite.favoriteId)) }) {
                        MenuIcon(
                            iconResource = Res.drawable.ic_close,
                            contentDescription = stringResource(
                                resource = Res.string.remove_favorite,
                                favoriteChoice.favorite.locality
                            )
                        )
                    }
                },
                onClick = { onEvent(WeatherForecastEvent.OnFavoriteClicked(favorite = favoriteChoice.favorite)) }
            )
        }
        HorizontalDivider(color = VadretTheme.colors.divider)
        DropdownMenuItem(
            text = { MenuText(text = stringResource(resource = Res.string.add_place)) },
            leadingIcon = { MenuIcon(iconResource = Res.drawable.ic_add) },
            onClick = { onEvent(WeatherForecastEvent.OnAddPlaceClicked) }
        )
    }
}

@Composable
private fun MenuText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = VadretTheme.textStyles.body,
        color = VadretTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

@Composable
private fun SelectionMark(
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    if (isSelected) {
        Icon(
            painter = painterResource(resource = Res.drawable.ic_check),
            contentDescription = stringResource(resource = Res.string.selected),
            tint = VadretTheme.colorScheme.primary,
            modifier = modifier.size(size = menuIconSize)
        )
    } else {
        Spacer(modifier = modifier.size(size = menuIconSize))
    }
}

@Composable
private fun MenuIcon(
    iconResource: DrawableResource,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    Icon(
        painter = painterResource(resource = iconResource),
        contentDescription = contentDescription,
        tint = VadretTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.size(size = menuIconSize)
    )
}