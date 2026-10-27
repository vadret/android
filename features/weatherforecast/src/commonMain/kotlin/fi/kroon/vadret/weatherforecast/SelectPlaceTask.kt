package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either

internal interface SelectPlaceTask {

    suspend operator fun invoke(autoCompleteItem: AutoCompleteItem): Either<Failure, Unit>
}