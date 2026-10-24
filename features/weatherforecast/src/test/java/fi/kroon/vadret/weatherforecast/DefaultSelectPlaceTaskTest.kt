package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.location.model.StoredLocation
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asLeft
import io.github.sphrak.either.extension.asRight
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultSelectPlaceTaskTest {

    private val locationRepository = FakeLocationRepository(isAutomatic = true.asRight())
    private val selectPlaceTask: SelectPlaceTask = DefaultSelectPlaceTask(locationRepository = locationRepository)

    @Test
    fun `the place is stored and the location stops being automatic`() = runTest {
        assertEquals(expected = Either.Right(b = Unit), actual = selectPlaceTask(autoCompleteItem = kiruna))
        assertEquals(expected = listOf("stored=Kiruna", "automatic=false"), actual = locationRepository.writes)
        assertEquals<Either<Failure, StoredLocation>>(
            expected = StoredLocation(
                locality = "Kiruna",
                municipality = "Kiruna",
                county = "Norrbotten",
                latitude = 67.8558,
                longitude = 20.2253
            ).asRight(),
            actual = locationRepository.getStoredLocation()
        )
    }

    @Test
    fun `a place that cannot be stored leaves the mode alone`() = runTest {
        val failure = Failure(errorCode = ErrorCode.STORAGE_FAILED)
        locationRepository.setStoredLocationResult = failure.asLeft()

        assertEquals(expected = Either.Left(a = failure), actual = selectPlaceTask(autoCompleteItem = kiruna))
        assertEquals(expected = listOf("stored=Kiruna"), actual = locationRepository.writes)
        assertEquals<Either<Failure, Boolean>>(expected = true.asRight(), actual = locationRepository.isAutomatic())
    }
}