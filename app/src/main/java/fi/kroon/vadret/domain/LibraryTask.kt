package fi.kroon.vadret.domain

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.exception.map
import fi.kroon.vadret.data.library.LibraryRepository
import fi.kroon.vadret.data.library.local.LibraryEntity
import io.reactivex.Single
import javax.inject.Inject

class LibraryTask @Inject constructor(
    private val libraryRepository: LibraryRepository
) {

    operator fun invoke(): Single<Either<Failure, List<LibraryEntity>>> =
        libraryRepository
            .get()
            .map { either ->
                either.map { thirdPartyList ->
                    thirdPartyList.sortedWith(
                        compareBy { thirdParty ->
                            thirdParty.title
                        }
                    )
                }
            }
}