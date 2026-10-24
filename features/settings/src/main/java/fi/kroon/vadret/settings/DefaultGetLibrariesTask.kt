package fi.kroon.vadret.settings

import fi.kroon.vadret.library.LibraryRepository
import fi.kroon.vadret.library.model.Library
import io.github.sphrak.either.Either
import javax.inject.Inject

internal class DefaultGetLibrariesTask @Inject constructor(
    private val libraryRepository: LibraryRepository
) : GetLibrariesTask {

    override fun invoke(): Either<Nothing, List<Library>> =
        libraryRepository.getLibraries()
}