package fi.kroon.vadret.domain.weatherforecastwidget.shared

import androidx.recyclerview.widget.DiffUtil
import fi.kroon.vadret.data.autocomplete.AutoCompleteRepository
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.functional.map
import fi.kroon.vadret.presentation.weatherforecast.autocomplete.AutoCompleteDiffUtil
import fi.kroon.vadret.util.DEFAULT_AUTOCOMPLETE_LIMIT
import fi.kroon.vadret.util.extension.empty
import io.reactivex.Single
import javax.inject.Inject
import timber.log.Timber

class GetWidgetAutoCompleteService @Inject constructor(
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

                val newFilteredList: List<AutoCompleteItem> = if (text != String.empty()) {
                    currentFilteredList
                        .filter { city ->
                            city.locality.startsWith(text, true)
                        }
                        .take(DEFAULT_AUTOCOMPLETE_LIMIT)
                        .toList()
                } else {
                    emptyList()
                }

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
        }
}