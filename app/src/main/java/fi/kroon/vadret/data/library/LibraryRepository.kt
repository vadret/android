package fi.kroon.vadret.data.library

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.library.local.LibraryLocalDataSource
import fi.kroon.vadret.data.library.model.Library
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class LibraryRepository @Inject constructor(
    private val libraryLocalDataSource: LibraryLocalDataSource
) {
    operator fun invoke(): Single<Either<Failure, List<Library>>> =
        libraryLocalDataSource()
            .doOnError {
                Timber.e("$it")
            }
}