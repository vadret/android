package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.weatherforecast.local.WeatherForecastLocalKeyValueDataSource
import fi.kroon.vadret.util.COUNTY_KEY
import fi.kroon.vadret.util.LATITUDE_KEY
import fi.kroon.vadret.util.LOCALITY_KEY
import fi.kroon.vadret.util.LONGITUDE_KEY
import fi.kroon.vadret.util.MUNICIPALITY_KEY
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import javax.inject.Inject

class SetLocationInformationTask @Inject constructor(
    private val repo: WeatherForecastLocalKeyValueDataSource
) {

    /**
     *  Stores every field and returns the first failure, if any.
     */
    operator fun invoke(autoCompleteItem: AutoCompleteItem): Either<Failure, Unit> =
        listOf(
            repo.putString(LATITUDE_KEY, autoCompleteItem.latitude.toString()),
            repo.putString(LONGITUDE_KEY, autoCompleteItem.longitude.toString()),
            repo.putString(LOCALITY_KEY, autoCompleteItem.locality),
            repo.putString(MUNICIPALITY_KEY, autoCompleteItem.municipality),
            repo.putString(COUNTY_KEY, autoCompleteItem.county)
        ).firstOrNull { result: Either<Failure, Unit> -> result.isLeft }
            ?: Unit.asRight()
}