package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.forecastpreference.model.ForecastPreferences
import io.github.sphrak.either.Either
import kotlinx.coroutines.flow.Flow

internal interface ObserveForecastPreferencesTask {

    operator fun invoke(): Flow<Either<Failure, ForecastPreferences>>
}