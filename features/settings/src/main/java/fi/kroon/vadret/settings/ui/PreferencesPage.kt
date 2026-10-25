package fi.kroon.vadret.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import fi.kroon.vadret.core.R
import fi.kroon.vadret.settings.Settings
import fi.kroon.vadret.settings.SettingsEvent
import fi.kroon.vadret.settings.SettingsState
import fi.kroon.vadret.settings.extension.labelResourceId
import fi.kroon.vadret.settings.model.LocationRow
import fi.kroon.vadret.ui.VadretCard
import fi.kroon.vadret.ui.VadretTheme
import kotlinx.collections.immutable.ImmutableList

private const val FULL_ALPHA = 1f
private const val DISABLED_ALPHA = 0.38f
private const val HIDDEN_ALPHA = 0f
private const val LOCATION_HEADER_KEY = "location-header"
private const val GPS_KEY = "gps"
private const val DISPLAY_HEADER_KEY = "display-header"
private const val THEME_KEY = "theme"
private const val CURRENT_LOCATION_HEADER_KEY = "current-location-header"
private const val CURRENT_LOCATION_KEY = "current-location"

private enum class PreferenceContentType {
    HEADER,
    CARD
}

@Composable
internal fun PreferencesPage(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val settings: Settings = state.settings ?: return

    LazyColumn(
        contentPadding = PaddingValues(
            start = VadretTheme.spacing.large,
            end = VadretTheme.spacing.large,
            bottom = VadretTheme.spacing.large
        ),
        modifier = modifier.fillMaxSize()
    ) {
        item(key = LOCATION_HEADER_KEY, contentType = PreferenceContentType.HEADER) {
            SettingsSectionHeader(text = stringResource(id = R.string.setting_location_title))
        }
        item(key = GPS_KEY, contentType = PreferenceContentType.CARD) {
            VadretCard {
                SwitchRow(
                    title = stringResource(id = R.string.use_gps_by_default),
                    checkedSummary = stringResource(id = R.string.gps_is_being_used),
                    uncheckedSummary = stringResource(id = R.string.gps_is_not_being_used),
                    isChecked = settings.isAutomaticLocation,
                    onCheckedChange = { isChecked: Boolean ->
                        onEvent(SettingsEvent.OnLocationModeToggled(isAutomaticLocation = isChecked))
                    }
                )
            }
        }
        item(key = DISPLAY_HEADER_KEY, contentType = PreferenceContentType.HEADER) {
            SettingsSectionHeader(text = stringResource(id = R.string.display))
        }
        item(key = THEME_KEY, contentType = PreferenceContentType.CARD) {
            VadretCard {
                SettingsValueRow(
                    title = stringResource(id = R.string.select_a_theme),
                    value = stringResource(id = settings.theme.labelResourceId()),
                    trailingIconResourceId = R.drawable.ic_chevron_right,
                    onClick = { onEvent(SettingsEvent.OnThemeRowClicked) }
                )
            }
        }
        item(key = CURRENT_LOCATION_HEADER_KEY, contentType = PreferenceContentType.HEADER) {
            SettingsSectionHeader(text = stringResource(id = R.string.setting_current_location))
        }
        item(key = CURRENT_LOCATION_KEY, contentType = PreferenceContentType.CARD) {
            LocationCard(
                locationRows = state.locationRows,
                isAutomaticLocation = settings.isAutomaticLocation
            )
        }
    }
}

@Composable
private fun LocationCard(
    locationRows: ImmutableList<LocationRow>,
    isAutomaticLocation: Boolean,
    modifier: Modifier = Modifier
) {
    VadretCard(modifier = modifier.alpha(alpha = locationAlpha(isAutomaticLocation = isAutomaticLocation))) {
        locationRows.forEachIndexed { index: Int, locationRow: LocationRow ->
            if (index > 0) {
                SettingsRowDivider()
            }
            SettingsValueRow(
                title = stringResource(id = locationRow.titleResourceId),
                value = locationRow.value
            )
        }
    }
}

@Composable
private fun SwitchRow(
    title: String,
    checkedSummary: String,
    uncheckedSummary: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(all = VadretTheme.spacing.large)
    ) {
        Column(
            modifier = Modifier
                .weight(weight = 1f)
                .padding(end = VadretTheme.spacing.large)
        ) {
            Text(
                text = title,
                style = VadretTheme.textStyles.listItem,
                color = VadretTheme.colorScheme.onSurface
            )
            Box {
                SwitchSummary(
                    text = checkedSummary,
                    isShown = isChecked
                )
                SwitchSummary(
                    text = uncheckedSummary,
                    isShown = !isChecked
                )
            }
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = VadretTheme.colorScheme.onSurfaceVariant,
                uncheckedBorderColor = VadretTheme.colorScheme.onSurfaceVariant,
                uncheckedTrackColor = VadretTheme.colorScheme.outlineVariant
            )
        )
    }
}

@Composable
private fun SwitchSummary(
    text: String,
    isShown: Boolean,
    modifier: Modifier = Modifier
) {
    val visibilityModifier: Modifier = if (isShown) {
        Modifier
    } else {
        Modifier
            .alpha(alpha = HIDDEN_ALPHA)
            .clearAndSetSemantics { }
    }
    Text(
        text = text,
        style = VadretTheme.textStyles.detail,
        color = VadretTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.then(other = visibilityModifier)
    )
}

private fun locationAlpha(isAutomaticLocation: Boolean): Float =
    if (isAutomaticLocation) {
        DISABLED_ALPHA
    } else {
        FULL_ALPHA
    }