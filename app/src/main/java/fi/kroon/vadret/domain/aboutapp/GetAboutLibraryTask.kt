package fi.kroon.vadret.domain.aboutapp

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.functional.map
import fi.kroon.vadret.data.library.LibraryRepository
import fi.kroon.vadret.data.library.model.Library
import io.reactivex.Single
import javax.inject.Inject

class GetAboutLibraryTask @Inject constructor(
    private val libraryRepository: LibraryRepository
) {

    operator fun invoke(): Single<Either<Failure, List<Library>>> =
        libraryRepository()
            .map { either: Either<Failure, List<Library>> ->
                either.map { libraryList: List<Library> ->
                    libraryList.sortedWith(
                        compareBy { library: Library ->
                            library.title
                        }
                    )
                }
            }
}