package fi.kroon.vadret.presentation.viewmodel

import fi.kroon.vadret.data.DEFAULT_SUGGESTION_LIMIT
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.exception.map
import fi.kroon.vadret.domain.SuggestionUseCase
import fi.kroon.vadret.utils.Schedulers
import io.reactivex.Single
import javax.inject.Inject

class SuggestionViewModel @Inject constructor(
    private val schedulers: Schedulers,
    private val suggestionUseCase: SuggestionUseCase
) : BaseViewModel() {

    var state = State()

    operator fun invoke(text: String): Single<Either<Failure, State>> = suggestionUseCase
        .get()
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