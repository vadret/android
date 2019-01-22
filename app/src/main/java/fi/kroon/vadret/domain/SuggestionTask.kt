package fi.kroon.vadret.domain

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.suggestion.SuggestionFailure
import fi.kroon.vadret.data.suggestion.SuggestionRepository
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class SuggestionTask @Inject constructor(
    private val suggestionRepository: SuggestionRepository
) {

    /**
     * @TODO add schedulers
     */
    operator fun invoke(): Single<Either<Failure, List<String>>> =
        suggestionRepository
            .get()
            .doOnError {
                Timber.e("$it")
            }.onErrorReturn {
                Either.left(SuggestionFailure.SuggestionsNotAvailable())
            }
}