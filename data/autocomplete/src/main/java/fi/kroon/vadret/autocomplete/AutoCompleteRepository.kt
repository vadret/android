package fi.kroon.vadret.autocomplete

import android.content.Context
import fi.kroon.vadret.autocomplete.local.DefaultAutoCompleteLocalDataSource
import fi.kroon.vadret.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.data.autocomplete.R
import io.github.sphrak.either.Either
import kotlinx.coroutines.CoroutineDispatcher

interface AutoCompleteRepository {

    /**
     *  At most [limit] places whose name starts with [prefix],
     *  ignoring case, in the alphabetical order of the bundled list.
     */
    suspend fun search(
        prefix: String,
        limit: Int
    ): Either<Failure, List<AutoCompleteItem>>
}

fun AutoCompleteRepository(
    context: Context,
    coroutineDispatcher: CoroutineDispatcher
): AutoCompleteRepository =
    DefaultAutoCompleteRepository(
        autoCompleteLocalDataSource = DefaultAutoCompleteLocalDataSource(
            openPlaces = { context.resources.openRawResource(R.raw.sweden) },
            coroutineDispatcher = coroutineDispatcher
        )
    )