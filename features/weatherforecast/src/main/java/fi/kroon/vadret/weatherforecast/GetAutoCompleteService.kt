package fi.kroon.vadret.weatherforecast

import androidx.recyclerview.widget.DiffUtil
import fi.kroon.vadret.data.autocomplete.AutoCompleteRepository
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.util.DEFAULT_AUTOCOMPLETE_LIMIT
import fi.kroon.vadret.weatherforecast.autocomplete.AutoCompleteDiffUtil
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.onResult
import io.github.sphrak.either.map
import timber.log.Timber
import javax.inject.Inject

class GetAutoCompleteService @Inject constructor(
    private val autoCompleteRepository: AutoCompleteRepository
) {

    data class Data(
        val currentFilteredList: List<AutoCompleteItem> = listOf(),
        val newFilteredList: List<AutoCompleteItem> = listOf(),
        val diffResult: DiffUtil.DiffResult? = null
    )

    /**
     *  Returns null when there is nothing to suggest, either
     *  because nothing matched or the city list failed to load.
     */
    suspend operator fun invoke(text: String): Either<Failure, Data>? =
        autoCompleteRepository()
            .map { currentFilteredList ->
                val state = Data(currentFilteredList = currentFilteredList)
                val newFilteredList = currentFilteredList
                    .filter { city ->
                        city.locality.startsWith(text, true)
                    }
                    .take(DEFAULT_AUTOCOMPLETE_LIMIT)
                    .toList()
                state.copy(newFilteredList = newFilteredList)
            }.map { state ->
                val diffResult = DiffUtil
                    .calculateDiff(
                        AutoCompleteDiffUtil(
                            state.currentFilteredList,
                            state.newFilteredList
                        )
                    )
                state.copy(diffResult = diffResult)
            }.takeIf { result: Either<Failure, Data> ->
                result.onResult(
                    { failure: Failure ->
                        Timber.e("$failure")
                        false
                    },
                    { data: Data ->
                        data.newFilteredList
                            .isNotEmpty()
                    }
                )
            }
}