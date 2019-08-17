package fi.kroon.vadret.domain.weatherforecast

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.weatherforecast.local.WeatherForecastLocalKeyValueDataSource
import fi.kroon.vadret.util.AUTOMATIC_LOCATION_MODE_KEY
import io.reactivex.Single
import javax.inject.Inject
import timber.log.Timber

class SetLocationModeTask @Inject constructor(
    private val repo: WeatherForecastLocalKeyValueDataSource
) {

    operator fun invoke(value: Boolean): Single<Either<Failure, Unit>> {
        Timber.d("Updating settings: Setting $AUTOMATIC_LOCATION_MODE_KEY to $value")
        return repo.putBoolean(AUTOMATIC_LOCATION_MODE_KEY, value)
    }
}