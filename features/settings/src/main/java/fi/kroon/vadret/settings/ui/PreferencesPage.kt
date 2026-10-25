package fi.kroon.vadret.settings.ui

import androidx.compose.foundation.clickable
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
import fi.kroon.vadret.core.R
import fi.kroon.vadret.settings.Settings
import fi.kroon.vadret.settings.SettingsEvent
import fi.kroon.vadret.settings.SettingsState
import fi.kroon.vadret.settings.model.LocationRow
import fi.kroon.vadret.theme.model.Theme
import fi.kroon.vadret.ui.VadretCard
import fi.kroon.vadret.ui.VadretTheme
import kotlinx.collections.immutable.ImmutableList

private const val DISABLED_ALPHA = 0.38f

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
        item(key = "location-header", contentType = PreferenceContentType.HEADER) {
            SettingsSectionHeader(text = stringResource(id = R.string.setting_location_title))
        }
        item(key = "gps", contentType = PreferenceContentType.CARD) {
            VadretCard {
                SwitchRow(
                    title = stringResource(id = R.string.use_gps_by_default),
                    summary = stringResource(id = automaticLocationSummaryResourceId(isAutomaticLocation = settings.isAutomaticLocation)),
                    isChecked = settings.isAutomaticLocation,
                    onCheckedChange = { isChecked: Boolean -> onEvent(SettingsEvent.OnLocationModeToggled(isAutomaticLocation = isChecked)) }
                )
            }
        }
        item(key = "display-header", contentType = PreferenceContentType.HEADER) {
            SettingsSectionHeader(text = stringResource(id = R.string.display))
        }
        item(key = "theme", contentType = PreferenceContentType.CARD) {
            VadretCard {
                SettingsValueRow(
                    title = stringResource(id = R.string.select_a_theme),
                    value = stringResource(id = settings.theme.labelResourceId()),
                    trailingIconResourceId = R.drawable.ic_chevron_right,
                    onClick = { onEvent(SettingsEvent.OnThemeRowClicked) }
                )
            }
        }
        item(key = "current-location-header", contentType = PreferenceContentType.HEADER) {
            SettingsSectionHeader(text = stringResource(id = R.string.setting_current_location))
        }
        item(key = "current-location", contentType = PreferenceContentType.CARD) {
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
    summary: String,
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
            Text(
                text = summary,
                style = VadretTheme.textStyles.detail,
                color = VadretTheme.colorScheme.onSurfaceVariant
            )
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

private fun automaticLocationSummaryResourceId(isAutomaticLocation: Boolean): Int =
    if (isAutomaticLocation) {
        R.string.gps_is_being_used
    } else {
        R.string.gps_is_not_being_used
    }

private fun locationAlpha(isAutomaticLocation: Boolean): Float =
    if (isAutomaticLocation) {
        DISABLED_ALPHA
    } else {
        1f
    }

internal fun Theme.labelResourceId(): Int =
    when (this) {
        Theme.LIGHT -> R.string.light_key
        Theme.DARK -> R.string.dark_key
        Theme.AMOLED -> R.string.amoled_key
    }