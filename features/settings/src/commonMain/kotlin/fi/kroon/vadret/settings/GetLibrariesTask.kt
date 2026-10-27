package fi.kroon.vadret.settings

import fi.kroon.vadret.library.model.Library
import io.github.sphrak.either.Either

internal interface GetLibrariesTask {
    operator fun invoke(): Either<Nothing, List<Library>>
}