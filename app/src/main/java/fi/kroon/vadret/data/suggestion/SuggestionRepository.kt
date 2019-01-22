package fi.kroon.vadret.data.suggestion

import fi.kroon.vadret.R
import fi.kroon.vadret.data.common.LocalFileDataSource
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class SuggestionRepository @Inject constructor(
    private val localFileReader: LocalFileDataSource
) {
    fun get(): Single<Either<Failure, List<String>>> {
        return localFileReader.readList(
            R.raw.sweden
        ).doOnError {
            Timber.e("$it")
        }.onErrorReturn {
            Either.left(SuggestionFailure.SuggestionsNotAvailable())
        }
    }
}