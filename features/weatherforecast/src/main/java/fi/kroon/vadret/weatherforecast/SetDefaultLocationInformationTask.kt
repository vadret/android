package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.weatherforecast.local.WeatherForecastLocalKeyValueDataSource
import fi.kroon.vadret.util.AUTOMATIC_LOCATION_MODE_KEY
import fi.kroon.vadret.util.COUNTY_KEY
import fi.kroon.vadret.util.DEFAULT_COUNTY
import fi.kroon.vadret.util.DEFAULT_LATITUDE
import fi.kroon.vadret.util.DEFAULT_LOCALITY
import fi.kroon.vadret.util.DEFAULT_LONGITUDE
import fi.kroon.vadret.util.DEFAULT_MUNICIPALITY
import fi.kroon.vadret.util.LATITUDE_KEY
import fi.kroon.vadret.util.LOCALITY_KEY
import fi.kroon.vadret.util.LONGITUDE_KEY
import fi.kroon.vadret.util.MUNICIPALITY_KEY
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import javax.inject.Inject

class SetDefaultLocationInformationTask @Inject constructor(
    private val repo: WeatherForecastLocalKeyValueDataSource
) {
    operator fun invoke(): Either<Failure, Unit> =
        listOf(
            repo.putString(LATITUDE_KEY, DEFAULT_LATITUDE),
            repo.putString(LONGITUDE_KEY, DEFAULT_LONGITUDE),
            repo.putString(LOCALITY_KEY, DEFAULT_LOCALITY),
            repo.putString(MUNICIPALITY_KEY, DEFAULT_MUNICIPALITY),
            repo.putString(COUNTY_KEY, DEFAULT_COUNTY),
            repo.putBoolean(AUTOMATIC_LOCATION_MODE_KEY, false)
        ).firstOrNull { result: Either<Failure, Unit> -> result.isLeft }
            ?: Unit.asRight()
}