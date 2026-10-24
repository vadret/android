package fi.kroon.vadret.weatherforecast.model

import fi.kroon.vadret.autocomplete.model.AutoCompleteItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class SearchState(
    val query: String = "",
    val suggestions: ImmutableList<AutoCompleteItem> = persistentListOf()
)