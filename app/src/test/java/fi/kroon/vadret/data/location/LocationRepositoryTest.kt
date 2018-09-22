package fi.kroon.vadret.data.location

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.location.model.Location
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LocationRepositoryTest {
    @Mock
    lateinit var mockLocationProvider: LocationProvider

    @Mock
    lateinit var mockLocation: Location

    private lateinit var testLocationRepository: LocationRepository

    @Before
    fun setup() {
        testLocationRepository = LocationRepository(mockLocationProvider)
    }

    @Test
    fun providerReturnsSingle_shouldPassResult() {
        val testLocationEither = createLocationEither(mockLocation)
        Mockito.doReturn(testLocationEither).`when`(mockLocationProvider).get()

        testLocationRepository
            .get()
            .test()
            .assertResult(testLocationEither)
    }

    @Test
    fun providerReturnsFailure_shouldPassFailure() {
        val testFailureEither = createFailureEither()
        Mockito.doReturn(testFailureEither).`when`(mockLocationProvider).get()

        testLocationRepository
            .get()
            .test()
            .assertResult(testFailureEither)
    }

    private fun createLocationEither(mockLocation: Location) =
        Either.Right(mockLocation) as Either<Failure, Location>

    private fun createFailureEither() =
        Either.Left(Failure.NetworkOfflineFailure())
}