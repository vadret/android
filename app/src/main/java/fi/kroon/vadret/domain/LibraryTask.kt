package fi.kroon.vadret.domain

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.functional.map
import fi.kroon.vadret.data.library.LibraryRepository
import fi.kroon.vadret.data.library.exception.LibraryFailure
import fi.kroon.vadret.data.library.model.Library
import fi.kroon.vadret.utils.extensions.asLeft
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class LibraryTask @Inject constructor(
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
            }.doOnError {
                Timber.e("$it")
            }.onErrorReturn {
                LibraryFailure.NoLibraryAvailable().asLeft()
            }
}