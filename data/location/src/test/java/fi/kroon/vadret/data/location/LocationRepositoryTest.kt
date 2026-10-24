package fi.kroon.vadret.data.location

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.location.exception.LocationFailure
import fi.kroon.vadret.data.location.local.LocationLocalDataSource
import fi.kroon.vadret.data.location.model.Location
import fi.kroon.vadret.util.extension.asLeft
import io.github.sphrak.either.Either
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertTrue

class LocationRepositoryTest {

    private val mockLocationLocalDataSource: LocationLocalDataSource = mock(LocationLocalDataSource::class.java)

    private val mockLocation: Location = mock(Location::class.java)

    private val testLocationRepository: LocationRepository = LocationRepository(mockLocationLocalDataSource)

    @Test
    fun `location is returned`() {
        doReturn(createLocationEither(mockLocation)).`when`(mockLocationLocalDataSource)()
        assertTrue(
            testLocationRepository()
                .let { it is Either.Right<Location> && it.b == mockLocation }
        )
    }

    @Test
    fun `location is not available`() {
        doReturn(getLocationFailure()).`when`(mockLocationLocalDataSource)()

        assertTrue(
            testLocationRepository()
                .let { it is Either.Left<Failure> && it.a is LocationFailure.LocationNotAvailable }
        )
    }

    private fun getLocationFailure() = LocationFailure
        .LocationNotAvailable
        .asLeft()

    private fun createLocationEither(location: Location): Either<Failure, Location> = Either.Right(location)
}