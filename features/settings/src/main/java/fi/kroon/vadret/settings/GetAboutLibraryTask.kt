package fi.kroon.vadret.settings

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.library.LibraryRepository
import fi.kroon.vadret.data.library.model.Library
import io.github.sphrak.either.Either
import io.github.sphrak.either.map
import javax.inject.Inject

class GetAboutLibraryTask @Inject constructor(
    private val libraryRepository: LibraryRepository
) {

    suspend operator fun invoke(): Either<Failure, List<Library>> =
        libraryRepository()
            .let { either: Either<Failure, List<Library>> ->
                either.map { libraryList: List<Library> ->
                    libraryList.sortedWith(
                        compareBy { library: Library ->
                            library.title
                        }
                    )
                }
            }
}