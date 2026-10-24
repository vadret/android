package fi.kroon.vadret.settings.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import fi.kroon.vadret.core.R
import fi.kroon.vadret.settings.model.AboutRow
import fi.kroon.vadret.ui.VadretTheme

private const val ABOUT_ROW_CONTENT_TYPE = "about-row"
private const val HEADER_CONTENT_TYPE = "header"
private val appIconSize = 64.dp
private val rowIconSize = 24.dp

@Composable
internal fun AboutPage(
    aboutRows: List<AboutRow>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(all = VadretTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(space = VadretTheme.spacing.small),
        modifier = modifier.fillMaxSize()
    ) {
        item(key = HEADER_CONTENT_TYPE, contentType = HEADER_CONTENT_TYPE) {
            AboutHeader()
        }
        items(
            items = aboutRows,
            key = { aboutRow: AboutRow -> aboutRow.titleResourceId },
            contentType = { ABOUT_ROW_CONTENT_TYPE }
        ) { aboutRow: AboutRow ->
            AboutRowCard(aboutRow = aboutRow)
        }
    }
}

@Composable
private fun AboutHeader(modifier: Modifier = Modifier) {
    val context: Context = LocalContext.current
    val appIcon: ImageBitmap = remember(context) {
        context.packageManager
            .getApplicationIcon(context.packageName)
            .toBitmap()
            .asImageBitmap()
    }
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(all = VadretTheme.spacing.large)
        ) {
            Image(
                bitmap = appIcon,
                contentDescription = null,
                modifier = Modifier.size(size = appIconSize)
            )
            Column(modifier = Modifier.padding(start = VadretTheme.spacing.large)) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = VadretTheme.typography.titleLarge
                )
                Text(
                    text = stringResource(id = R.string.about_app_description),
                    style = VadretTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic,
                    color = VadretTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AboutRowCard(
    aboutRow: AboutRow,
    modifier: Modifier = Modifier
) {
    val uriHandler: UriHandler = LocalUriHandler.current
    val url: String = stringResource(id = aboutRow.urlResourceId)
    val hintResourceId: Int? = aboutRow.hintResourceId
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { uriHandler.openUri(uri = url) }
                .padding(all = VadretTheme.spacing.large)
        ) {
            Icon(
                painter = painterResource(id = aboutRow.iconResourceId),
                contentDescription = null,
                tint = VadretTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(size = rowIconSize)
            )
            Column(modifier = Modifier.padding(start = VadretTheme.spacing.extraLarge)) {
                Text(
                    text = stringResource(id = aboutRow.titleResourceId),
                    style = VadretTheme.typography.titleMedium
                )
                if (hintResourceId != null) {
                    Text(
                        text = stringResource(id = hintResourceId),
                        style = VadretTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = VadretTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}