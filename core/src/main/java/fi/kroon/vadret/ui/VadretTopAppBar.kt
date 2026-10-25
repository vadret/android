package fi.kroon.vadret.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fi.kroon.vadret.core.R

private val topAppBarHeight = 56.dp
private val titleStartPadding = 20.dp

@Composable
fun VadretTopAppBar(
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(insets = WindowInsets.statusBars)
            .height(height = topAppBarHeight)
            .padding(
                start = titleStartPadding,
                end = VadretTheme.spacing.small
            )
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = VadretTheme.textStyles.title,
            color = VadretTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.weight(weight = 1f))
        actions()
    }
}