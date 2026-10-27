package fi.kroon.vadret.autocomplete.model

data class AutoCompleteItem(
    val locality: String,
    val municipality: String,
    val county: String,
    val latitude: Double,
    val longitude: Double
)