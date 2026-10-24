package fi.kroon.vadret.library

import fi.kroon.vadret.library.local.DefaultLibraryInMemoryDataSource
import fi.kroon.vadret.library.model.Library
import io.github.sphrak.either.Either

interface LibraryRepository {

    /**
     *  The list is part of the app and cannot fail, in title order.
     */
    fun getLibraries(): Either<Nothing, List<Library>>
}

fun LibraryRepository(): LibraryRepository =
    DefaultLibraryRepository(libraryInMemoryDataSource = DefaultLibraryInMemoryDataSource())