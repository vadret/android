package fi.kroon.vadret.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fi.kroon.vadret.core.R

private val topAppBarHeight = 56.dp

@Composable
fun VadretTopAppBar(
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                style = VadretTheme.textStyles.title
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