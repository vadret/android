package fi.kroon.vadret.settings.extension

import fi.kroon.vadret.library.model.Library
import fi.kroon.vadret.settings.model.LibraryGroup
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

internal fun List<Library>.toLibraryGroups(): ImmutableList<LibraryGroup> =
    groupBy { library: Library -> library.author }
        .map { (author: String, libraries: List<Library>) ->
            LibraryGroup(
                author = author,
                libraries = libraries.toImmutableList()
            )
        }.toImmutableList()