package fi.kroon.vadret.location

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.common.catch
import fi.kroon.vadret.common.either
import fi.kroon.vadret.location.extension.toFailure
import fi.kroon.vadret.location.extension.toLocation
import fi.kroon.vadret.location.extension.toLocationPreference
import fi.kroon.vadret.location.extension.toStoredLocation
import fi.kroon.vadret.location.extension.toStoredLocationEntity
import fi.kroon.vadret.location.local.DeviceLocationLocalDataSource
import fi.kroon.vadret.location.local.LocationPreferenceEntity
import fi.kroon.vadret.location.local.StoredLocationLocalDataSource
import fi.kroon.vadret.location.model.Location
import fi.kroon.vadret.location.model.LocationPreference
import fi.kroon.vadret.location.model.StoredLocation
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asLeft
import io.github.sphrak.either.extension.asRight
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

internal class DefaultLocationRepository(
    private val deviceLocationLocalDataSource: DeviceLocationLocalDataSource,
    private val storedLocationLocalDataSource: StoredLocationLocalDataSource
) : LocationRepository {

    override suspend fun getDeviceLocation(): Either<Failure, Location> =
        either { deviceLocationLocalDataSource.getLastKnownLocation().toLocation() }
            .catch { exception: Exception -> exception.toFailure() }

    override suspend fun isAutomatic(): Either<Failure, Boolean> =
        either { storedLocationLocalDataSource.isAutomatic() }
            .catch { exception: Exception -> exception.toFailure() }

    override suspend fun setAutomatic(isAutomatic: Boolean): Either<Failure, Unit> =
        either { storedLocationLocalDataSource.setAutomatic(isAutomatic = isAutomatic) }
            .catch { exception: Exception -> exception.toFailure() }

    override suspend fun getStoredLocation(): Either<Failure, StoredLocation> =
        either { storedLocationLocalDataSource.getStoredLocation().toStoredLocation() }
            .catch { exception: Exception -> exception.toFailure() }

    override fun observeLocationPreference(): Flow<Either<Failure, LocationPreference>> =
        storedLocationLocalDataSource
            .observeLocationPreference()
            .map<LocationPreferenceEntity, Either<Failure, LocationPreference>> { locationPreferenceEntity: LocationPreferenceEntity ->
                locationPreferenceEntity.toLocationPreference().asRight()
            }.catch { throwable: Throwable ->
                if (throwable is Exception) {
                    emit(throwable.toFailure().asLeft())
                } else {
                    throw throwable
                }
            }

    override suspend fun selectStoredLocation(storedLocation: StoredLocation): Either<Failure, Unit> =
        either { storedLocationLocalDataSource.selectStoredLocation(storedLocationEntity = storedLocation.toStoredLocationEntity()) }
            .catch { exception: Exception -> exception.toFailure() }
}