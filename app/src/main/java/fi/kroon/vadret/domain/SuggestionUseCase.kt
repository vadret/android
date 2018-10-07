package fi.kroon.vadret.domain

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.suggestion.SuggestionFailure
import fi.kroon.vadret.data.suggestion.SuggestionRepository
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class SuggestionUseCase @Inject constructor(
    private val suggestionRepository: SuggestionRepository
) {
    fun get(): Single<Either<Failure, List<String>>> {
        return suggestionRepository
            .get()
            .doOnEvent { t1, t2 ->
                Timber.d("T1: $t1, T2: $t2")
            }.doOnError {
                Timber.d("$it")
            }.onErrorReturn { _ ->
                Either.Left(SuggestionFailure.SuggestionsNotAvailable())
            }
    }
}