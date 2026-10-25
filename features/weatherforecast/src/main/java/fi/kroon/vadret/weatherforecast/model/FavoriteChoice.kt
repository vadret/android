package fi.kroon.vadret.weatherforecast.model

import fi.kroon.vadret.favorite.model.Favorite

data class FavoriteChoice(
    val favorite: Favorite,
    val isSelected: Boolean
)