package fi.kroon.vadret.library

import fi.kroon.vadret.library.local.DefaultLibraryInMemoryDataSource
import fi.kroon.vadret.library.model.Library
import io.github.sphrak.either.Either

interface LibraryRepository {

    fun getLibraries(): Either<Nothing, List<Library>>
}

fun LibraryRepository(): LibraryRepository =
    DefaultLibraryRepository(libraryInMemoryDataSource = DefaultLibraryInMemoryDataSource())