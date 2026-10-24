package fi.kroon.vadret.autocomplete.model

/**
 *  A Swedish place the user can search for.
 */
data class AutoCompleteItem(
    val locality: String,
    val municipality: String,
    val county: String,
    val latitude: Double,
    val longitude: Double
)