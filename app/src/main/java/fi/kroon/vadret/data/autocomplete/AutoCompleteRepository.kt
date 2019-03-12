package fi.kroon.vadret.data.autocomplete

import fi.kroon.vadret.R
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.data.common.LocalFileDataSource
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import io.reactivex.Single
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