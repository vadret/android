package fi.kroon.vadret.data.weatherforecast

import fi.kroon.vadret.BaseUnitTest
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.location.LocationRepository
import fi.kroon.vadret.data.location.exception.LocationFailure
import fi.kroon.vadret.data.location.model.Location
import fi.kroon.vadret.data.location.local.LocationLocalDataSource
import fi.kroon.vadret.utils.extensions.asLeft
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn

class LocationRepositoryTest : BaseUnitTest() {

    @Mock
    private lateinit var mockLocationLocalDataSource: LocationLocalDataSource

    @Mock
    private lateinit var mockLocation: Location

    private lateinit var testLocationRepository: LocationRepository

    @Before
    fun setup() {
        testLocationRepository = LocationRepository(mockLocationLocalDataSource)
    }

    @Test
    fun `location is returned`() {
        doReturn(createLocationEither(mockLocation)).`when`(mockLocationLocalDataSource)()
        testLocationRepository()
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Right<Location> && it.b == mockLocation }
    }

    @Test
    fun `location is not available`() {
        doReturn(getLocationFailure()).`when`(mockLocationLocalDataSource)()

        testLocationRepository()
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is LocationFailure.LocationNotAvailable }
    }

    private fun getLocationFailure() = LocationFailure.LocationNotAvailable().asLeft()
    private fun createLocationEither(location: Location): Either<Failure, Location> = Either.Right(location)
}