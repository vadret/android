package fi.kroon.vadret.location.model

/**
 *  The place searched for last, used while the location is not automatic.
 */
data class StoredLocation(
    val locality: String,
    val municipality: String,
    val county: String,
    val latitude: Double,
    val longitude: Double
)