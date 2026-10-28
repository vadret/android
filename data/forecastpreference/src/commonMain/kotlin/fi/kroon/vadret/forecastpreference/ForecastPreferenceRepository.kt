package fi.kroon.vadret.forecastpreference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.forecastpreference.local.DefaultForecastPreferenceLocalDataSource
import fi.kroon.vadret.forecastpreference.model.ForecastPreferences
import io.github.sphrak.either.Either
import kotlinx.coroutines.flow.Flow

interface ForecastPreferenceRepository {
    suspend fun getForecastPreferences(): Either<Failure, ForecastPreferences>
    fun observeForecastPreferences(): Flow<Either<Failure, ForecastPreferences>>
    suspend fun setHourlyForecastEnabled(isEnabled: Boolean): Either<Failure, Unit>
    suspend fun setExpandOnceEnabled(isEnabled: Boolean): Either<Failure, Unit>
}

fun ForecastPreferenceRepository(dataStore: DataStore<Preferences>): ForecastPreferenceRepository =
    DefaultForecastPreferenceRepository(
        forecastPreferenceLocalDataSource = DefaultForecastPreferenceLocalDataSource(dataStore = dataStore)
    )