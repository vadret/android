package fi.kroon.vadret.weatherforecast.extension

import fi.kroon.vadret.favorite.model.Favorite
import fi.kroon.vadret.location.model.LocationPreference
import fi.kroon.vadret.location.model.StoredLocation
import fi.kroon.vadret.weatherforecast.model.FavoriteChoice
import fi.kroon.vadret.weatherforecast.model.LocationChoices
import kotlinx.collections.immutable.toImmutableList

internal fun LocationPreference.toLocationChoices(favorites: List<Favorite>): LocationChoices =
    LocationChoices(
        isAutomatic = isAutomatic,
        favorites = favorites
            .map { favorite: Favorite ->
                FavoriteChoice(
                    favorite = favorite,
                    isSelected = !isAutomatic && favorite.isAt(storedLocation = storedLocation)
                )
            }.toImmutableList()
    )

private fun Favorite.isAt(storedLocation: StoredLocation): Boolean =
    locality == storedLocation.locality &&
        municipality == storedLocation.municipality &&
        county == storedLocation.county