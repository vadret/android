package fi.kroon.vadret.autocomplete.local

internal data class AutoCompleteItemEntity(
    val locality: String,
    val municipality: String,
    val county: String,
    val latitude: Double,
    val longitude: Double
)