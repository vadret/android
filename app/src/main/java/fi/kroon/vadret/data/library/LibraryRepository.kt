package fi.kroon.vadret.data.library

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.library.local.LibraryEntity
import fi.kroon.vadret.data.library.local.LibraryLocalDataSource
import io.reactivex.Single
import javax.inject.Inject

class LibraryRepository @Inject constructor(
    private val local: LibraryLocalDataSource
) {

    fun get(): Single<Either<Failure, List<LibraryEntity>>> =
        local.get()
}