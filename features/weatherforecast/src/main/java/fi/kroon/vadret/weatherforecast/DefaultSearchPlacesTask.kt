package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.autocomplete.AutoCompleteRepository
import fi.kroon.vadret.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either
import javax.inject.Inject

/**
 *  As many suggestions as fit above the keyboard.
 */
private const val SUGGESTION_LIMIT = 10

internal class DefaultSearchPlacesTask @Inject constructor(
    private val autoCompleteRepository: AutoCompleteRepository
) : SearchPlacesTask {

    override suspend fun invoke(prefix: String): Either<Failure, List<AutoCompleteItem>> =
        autoCompleteRepository.search(
            prefix = prefix,
            limit = SUGGESTION_LIMIT
        )
}