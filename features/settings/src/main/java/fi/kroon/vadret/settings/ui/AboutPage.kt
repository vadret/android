package fi.kroon.vadret.settings.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import fi.kroon.vadret.core.R
import fi.kroon.vadret.settings.model.AboutRow
import fi.kroon.vadret.settings.model.AboutRowAction
import fi.kroon.vadret.ui.VadretCard
import fi.kroon.vadret.ui.VadretTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val HEADER_KEY = "header"
private const val ROWS_KEY = "rows"
private const val HEADER_CONTENT_TYPE = "header"
private const val ROWS_CONTENT_TYPE = "rows"
private val appIconSize = 56.dp
private val rowIconSize = 24.dp
private val trailingIconSize = 20.dp
private val rowMinimumHeight = 56.dp

@Composable
internal fun AboutPage(
    aboutRows: ImmutableList<AboutRow>,
    onChangelogClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(all = VadretTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(space = VadretTheme.spacing.medium),
        modifier = modifier.fillMaxSize()
    ) {
        item(key = HEADER_KEY, contentType = HEADER_CONTENT_TYPE) {
            AboutHeader()
        }
        item(key = ROWS_KEY, contentType = ROWS_CONTENT_TYPE) {
            VadretCard {
                aboutRows.forEachIndexed { index: Int, aboutRow: AboutRow ->
                    if (index > 0) {
                        SettingsRowDivider()
                    }
                    AboutRowItem(
                        aboutRow = aboutRow,
                        onClick = aboutRow.action.onClick(onChangelogClick = onChangelogClick)
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutHeader(modifier: Modifier = Modifier) {
    val context: Context = LocalContext.current
    val appIcon: ImageBitmap? by produceState<ImageBitmap?>(initialValue = null, context) {
        value = withContext(context = Dispatchers.Default) {
            context.packageManager
                .getApplicationIcon(context.packageName)
                .toBitmap()
                .asImageBitmap()
        }
    }
    val loadedAppIcon: ImageBitmap? = appIcon
    VadretCard(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(space = VadretTheme.spacing.large),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(all = VadretTheme.spacing.large)
        ) {
            if (loadedAppIcon != null) {
                Image(
                    bitmap = loadedAppIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(size = appIconSize)
                        .clip(shape = CircleShape)
                )
            } else {
                Spacer(modifier = Modifier.size(size = appIconSize))
            }
            Column {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = VadretTheme.textStyles.title,
                    color = VadretTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(id = R.string.about_app_description),
                    style = VadretTheme.textStyles.body,
                    color = VadretTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AboutRowAction.onClick(onChangelogClick: () -> Unit): () -> Unit {
    val uriHandler: UriHandler = LocalUriHandler.current
    return when (this) {
        is AboutRowAction.OpenUrl -> {
            val url: String = stringResource(id = urlResourceId)
            val openUrl: () -> Unit = { uriHandler.openUri(uri = url) }
            openUrl
        }

        AboutRowAction.ShowChangelog -> onChangelogClick
    }
}

@Composable
private fun AboutRowItem(
    aboutRow: AboutRow,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hintResourceId: Int? = aboutRow.hintResourceId
    val trailingIconResourceId: Int? = aboutRow.trailingIconResourceId
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = VadretTheme.spacing.large),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = rowMinimumHeight)
            .clickable(onClick = onClick)
            .padding(
                horizontal = VadretTheme.spacing.large,
                vertical = VadretTheme.spacing.medium
            )
    ) {
        Icon(
            painter = painterResource(id = aboutRow.iconResourceId),
            contentDescription = null,
            tint = VadretTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(size = rowIconSize)
        )
        Column(modifier = Modifier.weight(weight = 1f)) {
            Text(
                text = stringResource(id = aboutRow.titleResourceId),
                style = VadretTheme.textStyles.listItem,
                color = VadretTheme.colorScheme.onSurface
            )
            if (hintResourceId != null) {
                Text(
                    text = stringResource(id = hintResourceId),
                    style = VadretTheme.textStyles.detail,
                    color = VadretTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (trailingIconResourceId != null) {
            Icon(
                painter = painterResource(id = trailingIconResourceId),
                contentDescription = null,
                tint = VadretTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(size = trailingIconSize)
            )
        }
    }
}