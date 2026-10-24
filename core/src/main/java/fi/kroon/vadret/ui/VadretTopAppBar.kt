package fi.kroon.vadret.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import fi.kroon.vadret.core.R

/**
 *  [trailingText] shows the place the weather is for.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VadretTopAppBar(
    modifier: Modifier = Modifier,
    trailingText: String? = null
) {
    Column(modifier = modifier) {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.app_name))
            },
            actions = {
                if (trailingText != null) {
                    Text(
                        text = trailingText,
                        style = VadretTheme.typography.titleMedium,
                        color = VadretTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = VadretTheme.spacing.large)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = VadretTheme.colorScheme.surface
            )
        )
        HorizontalDivider()
    }
}