package fi.kroon.vadret.weatherforecast.model

import fi.kroon.vadret.autocomplete.model.AutoCompleteItem

data class SearchState(
    val query: String = "",
    val suggestions: List<AutoCompleteItem> = emptyList()
)