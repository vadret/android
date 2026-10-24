package fi.kroon.vadret.data.autocomplete

import fi.kroon.vadret.data.autocomplete.local.LocalFileDataSource
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.data.failure.Failure
import io.reactivex.Single
import se.fimbla.either.Either
import timber.log.Timber
import javax.inject.Inject

class AutoCompleteRepository @Inject constructor(
    private val localFileReader: LocalFileDataSource
) {
    operator fun invoke(): Single<Either<Failure, List<AutoCompleteItem>>> =
        localFileReader.readCsvList(
            R.raw.sweden
        ).doOnError {
            Timber.e("$it")
        }
}