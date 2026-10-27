package fi.kroon.vadret.library

import fi.kroon.vadret.library.local.LibraryInMemoryDataSource
import fi.kroon.vadret.library.model.Library
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asRight

internal class DefaultLibraryRepository(
    private val libraryInMemoryDataSource: LibraryInMemoryDataSource
) : LibraryRepository {

    override fun getLibraries(): Either<Nothing, List<Library>> =
        libraryInMemoryDataSource
            .getLibraries()
            .sortedBy { library: Library -> library.title }
            .asRight()
}