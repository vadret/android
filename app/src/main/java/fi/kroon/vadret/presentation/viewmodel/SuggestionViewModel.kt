package fi.kroon.vadret.presentation.viewmodel

import fi.kroon.vadret.data.DEFAULT_SUGGESTION_LIMIT
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.exception.map
import fi.kroon.vadret.data.suggestion.model.State
import fi.kroon.vadret.domain.SuggestionTask
import fi.kroon.vadret.utils.Schedulers
import io.reactivex.Single
import javax.inject.Inject

class SuggestionViewModel @Inject constructor(
    private val schedulers: Schedulers,
    private val suggestionTask: SuggestionTask
) : BaseViewModel() {

    var state = State()

    operator fun invoke(text: String): Single<Either<Failure, State>> = suggestionTask
        .invoke()
        .map { either ->
            either.map { loadedList ->
                state = state.copy(currentFilteredlist = loadedList)

                val newFilteredList = loadedList.filter { city ->
                    city.startsWith(text, true)
                }
                    .take(DEFAULT_SUGGESTION_LIMIT)
                    .toList()
                state.copy(newFilteredList = newFilteredList)
            }
        }
}