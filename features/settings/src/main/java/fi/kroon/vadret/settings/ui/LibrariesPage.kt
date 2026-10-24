package fi.kroon.vadret.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import fi.kroon.vadret.core.R
import fi.kroon.vadret.library.model.Library
import fi.kroon.vadret.ui.VadretTheme
import kotlinx.collections.immutable.ImmutableList

private const val LIBRARY_CONTENT_TYPE = "library"

@Composable
internal fun LibrariesPage(
    libraries: ImmutableList<Library>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(all = VadretTheme.spacing.small),
        verticalArrangement = Arrangement.spacedBy(space = VadretTheme.spacing.small),
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = libraries,
            key = { library: Library -> library.title },
            contentType = { LIBRARY_CONTENT_TYPE }
        ) { library: Library ->
            LibraryCard(library = library)
        }
    }
}

@Composable
private fun LibraryCard(
    library: Library,
    modifier: Modifier = Modifier
) {
    val uriHandler: UriHandler = LocalUriHandler.current
    val sourceUrl: String? = library.sourceUrl
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(all = VadretTheme.spacing.large)) {
            Text(
                text = library.title,
                style = VadretTheme.typography.titleLarge
            )
            Text(
                text = library.description,
                style = VadretTheme.typography.bodyMedium,
                color = VadretTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = VadretTheme.spacing.small)
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(space = VadretTheme.spacing.small)) {
                Button(onClick = { uriHandler.openUri(uri = library.projectUrl) }) {
                    Text(text = stringResource(id = R.string.project_third_party))
                }
                Button(onClick = { uriHandler.openUri(uri = library.licenseUrl) }) {
                    Text(text = stringResource(id = R.string.license_third_party))
                }
                if (sourceUrl != null) {
                    Button(onClick = { uriHandler.openUri(uri = sourceUrl) }) {
                        Text(text = stringResource(id = R.string.source_third_party))
                    }
                }
            }
        }
    }
}