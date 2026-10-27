package fi.kroon.vadret.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import fi.kroon.vadret.theme.model.Theme

class ThemePreviewParameterProvider : PreviewParameterProvider<Theme> {

    override val values: Sequence<Theme> = Theme.entries.asSequence()

    override fun getDisplayName(index: Int): String = Theme.entries[index].name
}