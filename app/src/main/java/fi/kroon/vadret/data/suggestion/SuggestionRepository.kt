package fi.kroon.vadret.data.suggestion

import fi.kroon.vadret.R
import fi.kroon.vadret.data.common.RawTextFileReader
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class SuggestionRepository @Inject constructor(
    private val fileReader: RawTextFileReader
) {
    fun get(): Single<Either<Failure, List<String>>> {
        return Single.just(
            fileReader.readFileAsList(R.raw.sweden)
        ).doOnEvent { t1, t2 ->
            Timber.d("T1: $t1, T2: $t2")
        }.doOnError {
            Timber.e("$it")
        }.onErrorReturn {
            Either.Left(SuggestionFailure.SuggestionsNotAvailable())
        }
    }
}