package fi.kroon.vadret.location.model

data class StoredLocation(
    val locality: String,
    val municipality: String,
    val county: String,
    val latitude: Double,
    val longitude: Double
)