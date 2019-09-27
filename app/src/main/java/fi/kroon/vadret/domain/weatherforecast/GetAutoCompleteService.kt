package fi.kroon.vadret.domain.weatherforecast

import androidx.recyclerview.widget.DiffUtil
import fi.kroon.vadret.data.autocomplete.AutoCompleteRepository
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.presentation.weatherforecast.autocomplete.AutoCompleteDiffUtil
import fi.kroon.vadret.util.DEFAULT_AUTOCOMPLETE_LIMIT
import io.github.sphrak.either.Either
import io.github.sphrak.either.map
import io.reactivex.Maybe
import javax.inject.Inject
import timber.log.Timber

class GetAutoCompleteService @Inject constructor(
    private val autoCompleteRepository: AutoCompleteRepository
) {

    data class Data(
        val currentFilteredList: List<AutoCompleteItem> = listOf(),
        val newFilteredList: List<AutoCompleteItem> = listOf(),
        val diffResult: DiffUtil.DiffResult? = null
    )

    operator fun invoke(text: String): Maybe<Either<Failure, Data>> =
        autoCompleteRepository().map { either ->

            either.map { currentFilteredList ->

                val state = Data(currentFilteredList = currentFilteredList)

                val newFilteredList = currentFilteredList
                    .filter { city ->
                        city.locality.startsWith(text, true)
                    }
                    .take(DEFAULT_AUTOCOMPLETE_LIMIT)
                    .toList()

                state.copy(newFilteredList = newFilteredList)
            }
        }.map { either: Either<Failure, Data> ->
            either.map { state ->

                val diffResult = DiffUtil
                    .calculateDiff(
                        AutoCompleteDiffUtil(
                            state.currentFilteredList,
                            state.newFilteredList
                        )
                    )
                state.copy(diffResult = diffResult)
            }
        }.filter { result: Either<Failure, Data> ->
            result.either(
                {
                    false
                },
                { data: Data ->
                    data.newFilteredList
                        .isNotEmpty()
                }
            )
        }.doOnError {
            Timber.e("$it")
        }
}