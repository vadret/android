package fi.kroon.vadret.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fi.kroon.vadret.core.resources.Res
import fi.kroon.vadret.core.resources.app_name
import org.jetbrains.compose.resources.stringResource

private val topAppBarHeight = 55.dp
private val titleStartPadding = 5.dp

@Composable
fun VadretTopAppBar(
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(resource = Res.string.app_name),
                style = VadretTheme.textStyles.title,
                modifier = Modifier.padding(start = titleStartPadding)
            )
        },
        actions = actions,
        expandedHeight = topAppBarHeight,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = VadretTheme.colorScheme.background,
            titleContentColor = VadretTheme.colorScheme.onBackground,
            actionIconContentColor = VadretTheme.colorScheme.onSurfaceVariant
        ),
        modifier = modifier
    )
}