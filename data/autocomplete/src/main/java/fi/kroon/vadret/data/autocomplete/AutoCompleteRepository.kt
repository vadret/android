package fi.kroon.vadret.data.autocomplete

import fi.kroon.vadret.data.autocomplete.local.LocalFileDataSource
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.data.failure.Failure
import io.github.sphrak.either.Either
import javax.inject.Inject

class AutoCompleteRepository @Inject constructor(
    private val localFileReader: LocalFileDataSource
) {
    suspend operator fun invoke(): Either<Failure, List<AutoCompleteItem>> =
        localFileReader.readCsvList(
            R.raw.sweden
        )
}