package fi.kroon.vadret.data.library

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.library.local.LibraryLocalDataSource
import fi.kroon.vadret.data.library.model.Library
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject

class LibraryRepository @Inject constructor(
    private val libraryLocalDataSource: LibraryLocalDataSource
) {
    operator fun invoke(): Single<Either<Failure, List<Library>>> =
        libraryLocalDataSource()
}