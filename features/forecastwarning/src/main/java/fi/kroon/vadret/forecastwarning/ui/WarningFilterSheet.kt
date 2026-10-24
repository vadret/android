package fi.kroon.vadret.forecastwarning.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fi.kroon.vadret.core.R
import fi.kroon.vadret.data.district.model.DistrictOptionEntity
import fi.kroon.vadret.data.feedsource.model.FeedSourceOptionEntity
import fi.kroon.vadret.forecastwarning.WarningEvent
import fi.kroon.vadret.forecastwarning.WarningMapper
import fi.kroon.vadret.forecastwarning.model.WarningFilter
import fi.kroon.vadret.ui.VadretTheme

private const val TITLE_CONTENT_TYPE = "title"
private const val CHIPS_CONTENT_TYPE = "chips"
private val chipStrokeWidth = 1.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WarningFilterSheet(
    filter: WarningFilter,
    onEvent: (WarningEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = { onEvent(WarningEvent.OnFilterDismissed) },
        modifier = modifier
    ) {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = VadretTheme.spacing.large),
            modifier = Modifier.weight(
                weight = 1f,
                fill = false
            )
        ) {
            item(key = "feed-source-title", contentType = TITLE_CONTENT_TYPE) {
                FilterTitle(titleResourceId = R.string.filter_feed_source)
            }
            item(key = "feed-source-chips", contentType = CHIPS_CONTENT_TYPE) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(space = VadretTheme.spacing.small)) {
                    filter.feedSourceOptions.forEach { option: FeedSourceOptionEntity ->
                        key(option.id) {
                            FilterOptionChip(
                                label = option.name,
                                isSelected = option.isEnabled,
                                chipColors = WarningMapper.toWarningSource(name = option.name).chipColors(),
                                onClick = { onEvent(WarningEvent.OnFeedSourceToggled(id = option.id)) }
                            )
                        }
                    }
                }
            }
            item(key = "district-title", contentType = TITLE_CONTENT_TYPE) {
                FilterTitle(titleResourceId = R.string.filter_district)
            }
            item(key = "district-chips", contentType = CHIPS_CONTENT_TYPE) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(space = VadretTheme.spacing.small)) {
                    filter.districtOptions.forEach { option: DistrictOptionEntity ->
                        key(option.id) {
                            FilterOptionChip(
                                label = option.name,
                                isSelected = option.isEnabled,
                                chipColors = districtChipColors(category = option.category),
                                onClick = { onEvent(WarningEvent.OnDistrictToggled(id = option.id)) }
                            )
                        }
                    }
                }
            }
        }
        val errorMessageResourceId: Int? = filter.errorMessageResourceId
        if (errorMessageResourceId != null) {
            Text(
                text = stringResource(id = errorMessageResourceId),
                style = VadretTheme.typography.bodyMedium,
                color = VadretTheme.colorScheme.error,
                modifier = Modifier.padding(
                    start = VadretTheme.spacing.large,
                    end = VadretTheme.spacing.large,
                    top = VadretTheme.spacing.medium
                )
            )
        }
        Button(
            onClick = { onEvent(WarningEvent.OnFilterApplied) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = VadretTheme.spacing.large)
        ) {
            Text(text = stringResource(id = R.string.filter_apply))
        }
    }
}

@Composable
private fun FilterTitle(
    titleResourceId: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(id = titleResourceId),
        style = VadretTheme.typography.titleLarge,
        color = VadretTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(
            top = VadretTheme.spacing.large,
            bottom = VadretTheme.spacing.small
        )
    )
}

@Composable
private fun FilterOptionChip(
    label: String,
    isSelected: Boolean,
    chipColors: ChipColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(text = label) },
        leadingIcon = if (isSelected) {
            {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check_black_24dp),
                    contentDescription = null,
                    modifier = Modifier.size(size = FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = chipColors.container,
            labelColor = VadretTheme.colors.chipText,
            selectedContainerColor = chipColors.container,
            selectedLabelColor = VadretTheme.colors.chipText,
            selectedLeadingIconColor = VadretTheme.colors.chipText
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = chipColors.stroke,
            selectedBorderColor = chipColors.stroke,
            borderWidth = chipStrokeWidth,
            selectedBorderWidth = chipStrokeWidth
        ),
        modifier = modifier
    )
}