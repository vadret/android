package fi.kroon.vadret.data.autocomplete

import fi.kroon.vadret.R
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.data.common.LocalFileDataSource
import fi.kroon.vadret.data.failure.Failure
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject
import timber.log.Timber

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