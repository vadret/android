package fi.kroon.vadret.autocomplete

import fi.kroon.vadret.autocomplete.local.DefaultAutoCompleteLocalDataSource
import fi.kroon.vadret.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either
import kotlinx.coroutines.CoroutineDispatcher

interface AutoCompleteRepository {

    suspend fun search(
        prefix: String,
        limit: Int
    ): Either<Failure, List<AutoCompleteItem>>
}

fun AutoCompleteRepository(
    readPlaces: suspend () -> String,
    coroutineDispatcher: CoroutineDispatcher
): AutoCompleteRepository =
    DefaultAutoCompleteRepository(
        autoCompleteLocalDataSource = DefaultAutoCompleteLocalDataSource(
            readPlaces = readPlaces,
            coroutineDispatcher = coroutineDispatcher
        )
    )