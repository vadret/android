package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either

internal interface SearchPlacesTask {

    suspend operator fun invoke(prefix: String): Either<Failure, List<AutoCompleteItem>>
}