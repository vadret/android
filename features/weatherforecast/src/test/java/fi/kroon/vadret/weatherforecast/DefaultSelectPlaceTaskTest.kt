package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.location.model.LocationPreference
import fi.kroon.vadret.location.model.StoredLocation
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asLeft
import io.github.sphrak.either.extension.asRight
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

private val kirunaStoredLocation = StoredLocation(
    locality = "Kiruna",
    municipality = "Kiruna",
    county = "Norrbotten",
    latitude = 67.8558,
    longitude = 20.2253
)

class DefaultSelectPlaceTaskTest {

    private val locationRepository = FakeLocationRepository(isAutomatic = true.asRight())
    private val favoriteRepository = FakeFavoriteRepository()
    private val selectPlaceTask: SelectPlaceTask = DefaultSelectPlaceTask(
        favoriteRepository = favoriteRepository,
        locationRepository = locationRepository
    )

    @Test
    fun `the place becomes a favorite and the selected location`() =
        runTest {
            assertEquals(expected = Either.Right(b = Unit), actual = selectPlaceTask(autoCompleteItem = kiruna))
            assertEquals(expected = listOf(kirunaFavorite), actual = favoriteRepository.favorites.value)
            assertEquals(expected = listOf("selected=Kiruna"), actual = locationRepository.writes)
            assertEquals(
                expected = LocationPreference(
                    isAutomatic = false,
                    storedLocation = kirunaStoredLocation
                ),
                actual = locationRepository.locationPreference.value
            )
        }

    @Test
    fun `picking a favorite place again keeps one favorite`() =
        runTest {
            selectPlaceTask(autoCompleteItem = kiruna)
            selectPlaceTask(autoCompleteItem = kiruna)

            assertEquals(expected = listOf(kirunaFavorite), actual = favoriteRepository.favorites.value)
        }

    @Test
    fun `a place that cannot be saved is not selected`() =
        runTest {
            val failure = Failure(errorCode = ErrorCode.STORAGE_FAILED)
            favoriteRepository.failure = failure

            assertEquals(expected = Either.Left(a = failure), actual = selectPlaceTask(autoCompleteItem = kiruna))
            assertEquals(expected = emptyList(), actual = locationRepository.writes)
            assertEquals<Either<Failure, Boolean>>(expected = true.asRight(), actual = locationRepository.isAutomatic())
        }

    @Test
    fun `a place that cannot be selected keeps the mode`() =
        runTest {
            val failure = Failure(errorCode = ErrorCode.STORAGE_FAILED)
            locationRepository.selectStoredLocationResult = failure.asLeft()

            assertEquals(expected = Either.Left(a = failure), actual = selectPlaceTask(autoCompleteItem = kiruna))
            assertEquals<Either<Failure, Boolean>>(expected = true.asRight(), actual = locationRepository.isAutomatic())
        }
}