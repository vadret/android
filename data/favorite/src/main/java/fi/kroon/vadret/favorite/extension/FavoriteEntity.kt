package fi.kroon.vadret.favorite.extension

import fi.kroon.vadret.favorite.local.FavoriteEntity
import fi.kroon.vadret.favorite.model.Favorite

internal fun FavoriteEntity.toFavorite(): Favorite =
    Favorite(
        favoriteId = favoriteId,
        locality = locality,
        municipality = municipality,
        county = county,
        latitude = latitude,
        longitude = longitude
    )