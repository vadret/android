package fi.kroon.vadret.domain

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.location.LocationRepository
import fi.kroon.vadret.data.location.exception.LocationFailure
import fi.kroon.vadret.data.location.model.Location
import fi.kroon.vadret.data.weather.model.Weather
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LocationUseCaseTest {
    @Mock
    lateinit var mockLocationRepository: LocationRepository

    @Mock
    lateinit var mockLocation: Location

    private lateinit var testUseCase: LocationUseCase

    private val testThrowable = Throwable()

    @Before
    fun setup() {
        testUseCase = LocationUseCase(mockLocationRepository)
    }

    @Test
    fun repositoryReturnsSingle_shouldPassResult() {
        val testLocationEither = createLocationEither(mockLocation)
        val testSingle = createLocationSingle(testLocationEither)
        Mockito.doReturn(testSingle).`when`(mockLocationRepository).get()

        testUseCase
            .get()
            .test()
            .assertResult(testLocationEither)
    }

    @Test
    fun repositoryReturnsFailure_shouldPassFailure() {
        val testFailureEither = createFailureEither()
        val testSingle = createFailureSingle(testFailureEither)
        Mockito.doReturn(testSingle).`when`(mockLocationRepository).get()

        testUseCase
            .get()
            .test()
            .assertResult(testFailureEither)
    }

    @Test
    fun repositoryThrowsException_shouldReturnIOExceptionEither() {
        val testSingle = createThrowableSingle()
        Mockito.doReturn(testSingle).`when`(mockLocationRepository).get()

        testUseCase
            .get()
            .test()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is LocationFailure.LocationNotReturnedByRepository }
    }

    private fun createFailureEither() =
        Either.Left(Failure.NetworkOfflineFailure())

    private fun createFailureSingle(failureEither: Either.Left<Failure>) =
        Single.just(failureEither)

    private fun createThrowableSingle() =
        Single.error<Either<Failure, Weather>>(testThrowable)

    private fun createLocationEither(location: Location) =
        Either.Right(location) as Either<Failure, Location>

    private fun createLocationSingle(value: Either<Failure, Location>) =
        Single.just(value)
}