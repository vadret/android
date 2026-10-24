package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.data.autocomplete.AutoCompleteRepository
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.util.DEFAULT_AUTOCOMPLETE_LIMIT
import io.github.sphrak.either.Either
import io.github.sphrak.either.map
import javax.inject.Inject

class GetAutoCompleteService @Inject constructor(
    private val autoCompleteRepository: AutoCompleteRepository
) {

    /**
     *  Places whose name starts with [text], empty when none do.
     */
    suspend operator fun invoke(text: String): Either<Failure, List<AutoCompleteItem>> =
        autoCompleteRepository()
            .map { autoCompleteItemList: List<AutoCompleteItem> ->
                autoCompleteItemList
                    .asSequence()
                    .filter { autoCompleteItem: AutoCompleteItem ->
                        autoCompleteItem.locality.startsWith(
                            prefix = text,
                            ignoreCase = true
                        )
                    }
                    .take(DEFAULT_AUTOCOMPLETE_LIMIT)
                    .toList()
            }
}