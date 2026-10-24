package fi.kroon.vadret.autocomplete.local

/**
 *  A row of the bundled list of Swedish places.
 */
internal data class AutoCompleteItemEntity(
    val locality: String,
    val municipality: String,
    val county: String,
    val latitude: Double,
    val longitude: Double
)