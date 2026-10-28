package fi.kroon.vadret.forecastpreference

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.common.catch
import fi.kroon.vadret.common.either
import fi.kroon.vadret.forecastpreference.extension.toFailure
import fi.kroon.vadret.forecastpreference.extension.toForecastPreferences
import fi.kroon.vadret.forecastpreference.local.ForecastPreferenceLocalDataSource
import fi.kroon.vadret.forecastpreference.local.ForecastPreferencesEntity
import fi.kroon.vadret.forecastpreference.model.ForecastPreferences
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asLeft
import io.github.sphrak.either.extension.asRight
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

internal class DefaultForecastPreferenceRepository(
    private val forecastPreferenceLocalDataSource: ForecastPreferenceLocalDataSource
) : ForecastPreferenceRepository {

    override suspend fun getForecastPreferences(): Either<Failure, ForecastPreferences> =
        either { forecastPreferenceLocalDataSource.getForecastPreferences().toForecastPreferences() }
            .catch { exception: Exception -> exception.toFailure() }

    override fun observeForecastPreferences(): Flow<Either<Failure, ForecastPreferences>> =
        forecastPreferenceLocalDataSource
            .observeForecastPreferences()
            .map<ForecastPreferencesEntity, Either<Failure, ForecastPreferences>> { forecastPreferencesEntity: ForecastPreferencesEntity ->
                forecastPreferencesEntity.toForecastPreferences().asRight()
            }.catch { throwable: Throwable ->
                if (throwable is Exception) {
                    emit(throwable.toFailure().asLeft())
                } else {
                    throw throwable
                }
            }

    override suspend fun setHourlyForecastEnabled(isEnabled: Boolean): Either<Failure, Unit> =
        either { forecastPreferenceLocalDataSource.setHourlyForecastEnabled(value = isEnabled) }
            .catch { exception: Exception -> exception.toFailure() }

    override suspend fun setExpandOnceEnabled(isEnabled: Boolean): Either<Failure, Unit> =
        either { forecastPreferenceLocalDataSource.setExpandOnceEnabled(value = isEnabled) }
            .catch { exception: Exception -> exception.toFailure() }
}