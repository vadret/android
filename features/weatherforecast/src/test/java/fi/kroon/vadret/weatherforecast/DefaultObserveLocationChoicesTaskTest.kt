package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.favorite.model.Favorite
import fi.kroon.vadret.location.model.LocationPreference
import fi.kroon.vadret.location.model.StoredLocation
import fi.kroon.vadret.weatherforecast.model.FavoriteChoice
import fi.kroon.vadret.weatherforecast.model.LocationChoices
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asLeft
import io.github.sphrak.either.extension.asRight
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

private val bergaDanderyd = Favorite(
    favoriteId = "berga-danderyd",
    locality = "Berga",
    municipality = "Danderyd",
    county = "Stockholms län",
    latitude = 59.4,
    longitude = 18.05
)

private val bergaLinkoping = bergaDanderyd.copy(
    favoriteId = "berga-linkoping",
    municipality = "Linköping",
    county = "Östergötlands län"
)

private fun Favorite.toStoredLocation(): StoredLocation =
    StoredLocation(
        locality = locality,
        municipality = municipality,
        county = county,
        latitude = latitude,
        longitude = longitude
    )

class DefaultObserveLocationChoicesTaskTest {

    private val locationRepository = FakeLocationRepository()
    private val favoriteRepository = FakeFavoriteRepository()
    private val observeLocationChoicesTask: ObserveLocationChoicesTask = DefaultObserveLocationChoicesTask(
        favoriteRepository = favoriteRepository,
        locationRepository = locationRepository
    )

    private fun prefer(
        isAutomatic: Boolean,
        favorite: Favorite
    ) {
        favoriteRepository.favorites.value = listOf(bergaDanderyd, bergaLinkoping)
        locationRepository.locationPreference.value = LocationPreference(
            isAutomatic = isAutomatic,
            storedLocation = favorite.toStoredLocation()
        )
    }

    @Test
    fun `automatic selects no favorite`() =
        runTest {
            prefer(
                isAutomatic = true,
                favorite = bergaDanderyd
            )

            assertEquals<Either<Failure, LocationChoices>>(
                expected = LocationChoices(
                    isAutomatic = true,
                    favorites = persistentListOf(
                        FavoriteChoice(favorite = bergaDanderyd, isSelected = false),
                        FavoriteChoice(favorite = bergaLinkoping, isSelected = false)
                    )
                ).asRight(),
                actual = observeLocationChoicesTask().first()
            )
        }

    @Test
    fun `the stored place selects only the favorite in the same municipality`() =
        runTest {
            prefer(
                isAutomatic = false,
                favorite = bergaLinkoping
            )

            assertEquals<Either<Failure, LocationChoices>>(
                expected = LocationChoices(
                    isAutomatic = false,
                    favorites = persistentListOf(
                        FavoriteChoice(favorite = bergaDanderyd, isSelected = false),
                        FavoriteChoice(favorite = bergaLinkoping, isSelected = true)
                    )
                ).asRight(),
                actual = observeLocationChoicesTask().first()
            )
        }

    @Test
    fun `a stored place that is no favorite selects nothing`() =
        runTest {
            prefer(
                isAutomatic = false,
                favorite = kirunaFavorite
            )

            assertEquals(
                expected = listOf(false, false),
                actual = observeLocationChoicesTask()
                    .first()
                    .successOrEmpty()
                    .map { favoriteChoice: FavoriteChoice -> favoriteChoice.isSelected }
            )
        }

    @Test
    fun `a favorite failure fails the choices`() =
        runTest {
            val failure = Failure(errorCode = ErrorCode.STORAGE_FAILED)
            favoriteRepository.failure = failure

            assertEquals<Either<Failure, LocationChoices>>(
                expected = failure.asLeft(),
                actual = observeLocationChoicesTask().first()
            )
        }

    private fun Either<Failure, LocationChoices>.successOrEmpty(): List<FavoriteChoice> =
        when (this) {
            is Either.Left -> emptyList()
            is Either.Right -> b.favorites
        }
}