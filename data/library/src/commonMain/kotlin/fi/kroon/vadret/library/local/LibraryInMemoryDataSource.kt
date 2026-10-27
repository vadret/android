package fi.kroon.vadret.library.local

import fi.kroon.vadret.library.model.Library

internal interface LibraryInMemoryDataSource {
    fun getLibraries(): List<Library>
}