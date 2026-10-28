package fi.kroon.vadret.weatherforecast.model

import fi.kroon.vadret.place.model.Place
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class SearchState(
    val query: String = "",
    val suggestions: ImmutableList<Place> = persistentListOf()
)