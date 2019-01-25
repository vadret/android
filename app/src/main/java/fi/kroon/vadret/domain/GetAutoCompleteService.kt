package fi.kroon.vadret.domain

import androidx.recyclerview.widget.DiffUtil
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.data.autocomplete.AutoCompleteRepository
import fi.kroon.vadret.data.autocomplete.exception.AutoCompleteFailure
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.functional.map
import fi.kroon.vadret.presentation.weatherforecast.autocomplete.AutoCompleteDiffUtil
import fi.kroon.vadret.utils.DEFAULT_AUTOCOMPLETE_LIMIT
import fi.kroon.vadret.utils.extensions.asLeft
import io.reactivex.Single
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

    operator fun invoke(text: String): Single<Either<Failure, Data>> =
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
        }.doOnError {
            Timber.e("$it")
        }.onErrorReturn {
            AutoCompleteFailure.AutoCompleteNotAvailable().asLeft()
        }
}