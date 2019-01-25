package fi.kroon.vadret.data.autocomplete.model

data class AutoCompleteItem(
    val locality: String,
    val municipality: String,
    val county: String,
    val latitude: Double,
    val longitude: Double,
    /**
     *   LocationMode defaults to true
     *   since as soon as an item is selected via search
     *   it switches to manual mode.
     */
    val locationMode: Boolean = true
)