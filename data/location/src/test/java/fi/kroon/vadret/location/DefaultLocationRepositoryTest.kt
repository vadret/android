package fi.kroon.vadret.location

import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.location.exception.LocationNotAvailableException
import fi.kroon.vadret.location.exception.LocationProviderDisabledException
import fi.kroon.vadret.location.local.DeviceLocationLocalDataSource
import fi.kroon.vadret.location.local.LocationEntity
import fi.kroon.vadret.location.local.StoredLocationEntity
import fi.kroon.vadret.location.local.StoredLocationLocalDataSource
import fi.kroon.vadret.location.model.Location
import fi.kroon.vadret.location.model.StoredLocation
import fi.kroon.vadret.logging.LogLevel
import fi.kroon.vadret.testing.LogLine
import fi.kroon.vadret.testing.withRecordedLogs
import io.github.sphrak.either.Either
import kotlinx.coroutines.test.runTest
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals

private class FakeDeviceLocationLocalDataSource(
    private val getLocation: () -> LocationEntity
) : DeviceLocationLocalDataSource {
    override suspend fun getLastKnownLocation(): LocationEntity = getLocation()
}

private class FakeStoredLocationLocalDataSource(
    var isAutomatic: Boolean = true,
    var storedLocationEntity: StoredLocationEntity = StoredLocationEntity(
        locality = "Stockholm",
        municipality = "Stockholm",
        county = "Stockholm",
        latitude = 59.3293,
        longitude = 18.0686
    )
) : StoredLocationLocalDataSource {
    override suspend fun isAutomatic(): Boolean = isAutomatic

    override suspend fun setAutomatic(isAutomatic: Boolean) {
        this.isAutomatic = isAutomatic
    }

    override suspend fun getStoredLocation(): StoredLocationEntity = storedLocationEntity

    override suspend fun setStoredLocation(storedLocationEntity: StoredLocationEntity) {
        this.storedLocationEntity = storedLocationEntity
    }
}

private class BrokenStoredLocationLocalDataSource : StoredLocationLocalDataSource {
    override suspend fun isAutomatic(): Boolean = throw IOException("corrupt")
    override suspend fun setAutomatic(isAutomatic: Boolean) = throw IOException("corrupt")
    override suspend fun getStoredLocation(): StoredLocationEntity = throw IOException("corrupt")
    override suspend fun setStoredLocation(storedLocationEntity: StoredLocationEntity) = throw IOException("corrupt")
}

class DefaultLocationRepositoryTest {

    private fun repository(
        getLocation: () -> LocationEntity = {
            LocationEntity(
                latitude = 57.7,
                longitude = 11.97
            )
        },
        storedLocationLocalDataSource: StoredLocationLocalDataSource = FakeStoredLocationLocalDataSource()
    ): LocationRepository =
        DefaultLocationRepository(
            deviceLocationLocalDataSource = FakeDeviceLocationLocalDataSource(getLocation = getLocation),
            storedLocationLocalDataSource = storedLocationLocalDataSource
        )

    @Test
    fun `the device location is returned`() = runTest {
        assertEquals(
            expected = Either.Right(
                b = Location(
                    latitude = 57.7,
                    longitude = 11.97
                )
            ),
            actual = repository().getDeviceLocation()
        )
    }

    @Test
    fun `device location failures are told apart`() = runTest {
        val cases: Map<Exception, ErrorCode> = mapOf(
            LocationProviderDisabledException() to ErrorCode.LOCATION_PROVIDER_DISABLED,
            LocationNotAvailableException() to ErrorCode.LOCATION_NOT_AVAILABLE,
            SecurityException("no permission") to ErrorCode.LOCATION_PERMISSION_MISSING
        )

        withRecordedLogs { recordingLogSink ->
            val actual: Map<Exception, Either<Failure, Location>> = cases.keys.associateWith { exception: Exception ->
                repository(getLocation = { throw exception }).getDeviceLocation()
            }

            assertEquals(
                expected = cases.mapValues { (_, errorCode: ErrorCode) -> Either.Left(a = Failure(errorCode = errorCode)) },
                actual = actual
            )
            assertEquals(
                expected = List(size = cases.size) { LogLevel.WARN },
                actual = recordingLogSink.lines.map { line: LogLine -> line.level }
            )
        }
    }

    @Test
    fun `an unexpected failure is unknown and logged as an error`() = runTest {
        withRecordedLogs { recordingLogSink ->
            assertEquals(
                expected = Either.Left(a = Failure.unknown()),
                actual = repository(getLocation = { throw IllegalStateException("bug") }).getDeviceLocation()
            )
            assertEquals(expected = LogLevel.ERROR, actual = recordingLogSink.lines.single().level)
        }
    }

    @Test
    fun `the location mode is read and written`() = runTest {
        val locationRepository: LocationRepository = repository()

        val before: Either<Failure, Boolean> = locationRepository.isAutomatic()
        locationRepository.setAutomatic(isAutomatic = false)

        assertEquals(expected = Either.Right(b = true), actual = before)
        assertEquals(expected = Either.Right(b = false), actual = locationRepository.isAutomatic())
    }

    @Test
    fun `a stored location reads back with numeric coordinates`() = runTest {
        val locationRepository: LocationRepository = repository()
        val storedLocation = StoredLocation(
            locality = "Kiruna",
            municipality = "Kiruna",
            county = "Norrbotten",
            latitude = 67.8558,
            longitude = 20.2253
        )

        locationRepository.setStoredLocation(storedLocation = storedLocation)

        assertEquals(expected = Either.Right(b = storedLocation), actual = locationRepository.getStoredLocation())
    }

    @Test
    fun `storage that cannot be read is a storage failure`() = runTest {
        val locationRepository: LocationRepository = DefaultLocationRepository(
            deviceLocationLocalDataSource = FakeDeviceLocationLocalDataSource(getLocation = { error("unused") }),
            storedLocationLocalDataSource = BrokenStoredLocationLocalDataSource()
        )

        withRecordedLogs {
            assertEquals(
                expected = Either.Left(a = Failure(errorCode = ErrorCode.STORAGE_FAILED)),
                actual = locationRepository.getStoredLocation()
            )
        }
    }
}