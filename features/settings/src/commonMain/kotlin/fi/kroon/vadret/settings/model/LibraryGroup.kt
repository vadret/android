package fi.kroon.vadret.settings.model

import fi.kroon.vadret.library.model.Library
import kotlinx.collections.immutable.ImmutableList

internal data class LibraryGroup(
    val author: String,
    val libraries: ImmutableList<Library>
)