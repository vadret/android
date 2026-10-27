package fi.kroon.vadret.autocomplete

import fi.kroon.vadret.autocomplete.extension.toAutoCompleteItem
import fi.kroon.vadret.autocomplete.extension.toFailure
import fi.kroon.vadret.autocomplete.local.AutoCompleteItemEntity
import fi.kroon.vadret.autocomplete.local.AutoCompleteLocalDataSource
import fi.kroon.vadret.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.common.catch
import fi.kroon.vadret.common.either
import io.github.sphrak.either.Either

internal class DefaultAutoCompleteRepository(
    private val autoCompleteLocalDataSource: AutoCompleteLocalDataSource
) : AutoCompleteRepository {

    override suspend fun search(
        prefix: String,
        limit: Int
    ): Either<Failure, List<AutoCompleteItem>> =
        either {
            autoCompleteLocalDataSource
                .search(
                    prefix = prefix,
                    limit = limit
                ).map { autoCompleteItemEntity: AutoCompleteItemEntity -> autoCompleteItemEntity.toAutoCompleteItem() }
        }.catch { exception: Exception -> exception.toFailure() }
}