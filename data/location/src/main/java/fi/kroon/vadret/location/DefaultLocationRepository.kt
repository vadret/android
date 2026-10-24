package fi.kroon.vadret.location

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.common.catch
import fi.kroon.vadret.common.either
import fi.kroon.vadret.location.extension.toFailure
import fi.kroon.vadret.location.extension.toLocation
import fi.kroon.vadret.location.extension.toStoredLocation
import fi.kroon.vadret.location.extension.toStoredLocationEntity
import fi.kroon.vadret.location.local.DeviceLocationLocalDataSource
import fi.kroon.vadret.location.local.StoredLocationLocalDataSource
import fi.kroon.vadret.location.model.Location
import fi.kroon.vadret.location.model.StoredLocation
import io.github.sphrak.either.Either

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

    override suspend fun setStoredLocation(storedLocation: StoredLocation): Either<Failure, Unit> =
        either { storedLocationLocalDataSource.setStoredLocation(storedLocationEntity = storedLocation.toStoredLocationEntity()) }
            .catch { exception: Exception -> exception.toFailure() }
}