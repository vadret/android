package fi.kroon.vadret.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
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
import fi.kroon.vadret.theme.model.Theme
import fi.kroon.vadret.ui.VadretTheme

private const val DISABLED_ALPHA = 0.38f

private enum class PreferenceContentType {
    HEADER,
    SWITCH,
    ROW,
    DIVIDER
}

@Composable
internal fun PreferencesPage(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val settings: Settings = state.settings ?: return
    val locationRows: List<Pair<Int, String>> = listOf(
        R.string.setting_city to settings.storedLocation.locality,
        R.string.setting_municipality to settings.storedLocation.municipality,
        R.string.setting_county to settings.storedLocation.county,
        R.string.setting_latitude to settings.storedLocation.latitude.toString(),
        R.string.setting_longitude to settings.storedLocation.longitude.toString()
    )

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item(key = "location-header", contentType = PreferenceContentType.HEADER) {
            SectionHeader(titleResourceId = R.string.setting_location_title)
        }
        item(key = "gps", contentType = PreferenceContentType.SWITCH) {
            SwitchRow(
                title = stringResource(id = R.string.use_gps_by_default),
                summary = stringResource(id = automaticLocationSummaryResourceId(isAutomaticLocation = settings.isAutomaticLocation)),
                isChecked = settings.isAutomaticLocation,
                onCheckedChange = { isChecked -> onEvent(SettingsEvent.OnLocationModeToggled(isAutomaticLocation = isChecked)) }
            )
        }
        item(key = "location-divider", contentType = PreferenceContentType.DIVIDER) {
            HorizontalDivider()
        }
        item(key = "display-header", contentType = PreferenceContentType.HEADER) {
            SectionHeader(titleResourceId = R.string.display)
        }
        item(key = "theme", contentType = PreferenceContentType.ROW) {
            PreferenceRow(
                title = stringResource(id = R.string.select_a_theme),
                summary = stringResource(id = settings.theme.labelResourceId()),
                onClick = { onEvent(SettingsEvent.OnThemeRowClicked) }
            )
        }
        item(key = "display-divider", contentType = PreferenceContentType.DIVIDER) {
            HorizontalDivider()
        }
        item(key = "current-location-header", contentType = PreferenceContentType.HEADER) {
            SectionHeader(titleResourceId = R.string.setting_current_location)
        }
        locationRows.forEach { (titleResourceId: Int, value: String) ->
            item(key = titleResourceId, contentType = PreferenceContentType.ROW) {
                PreferenceRow(
                    title = stringResource(id = titleResourceId),
                    summary = value,
                    modifier = Modifier.alpha(alpha = locationAlpha(isAutomaticLocation = settings.isAutomaticLocation))
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    titleResourceId: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(id = titleResourceId),
        style = VadretTheme.typography.titleSmall,
        color = VadretTheme.colorScheme.primary,
        modifier = modifier.padding(
            start = VadretTheme.spacing.large,
            end = VadretTheme.spacing.large,
            top = VadretTheme.spacing.extraLarge,
            bottom = VadretTheme.spacing.small
        )
    )
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
            .padding(
                horizontal = VadretTheme.spacing.large,
                vertical = VadretTheme.spacing.medium
            )
    ) {
        RowText(
            title = title,
            summary = summary,
            modifier = Modifier.weight(weight = 1f)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun PreferenceRow(
    title: String,
    summary: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val clickModifier: Modifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }
    RowText(
        title = title,
        summary = summary,
        modifier = modifier
            .fillMaxWidth()
            .then(clickModifier)
            .padding(
                horizontal = VadretTheme.spacing.large,
                vertical = VadretTheme.spacing.medium
            )
    )
}

@Composable
private fun RowText(
    title: String,
    summary: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = VadretTheme.typography.bodyLarge
        )
        Text(
            text = summary,
            style = VadretTheme.typography.bodyMedium,
            color = VadretTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun automaticLocationSummaryResourceId(isAutomaticLocation: Boolean): Int =
    if (isAutomaticLocation) {
        R.string.gps_is_being_used
    } else {
        R.string.gps_is_not_being_used
    }

/**
 *  The stored place is only used while GPS is off.
 */
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