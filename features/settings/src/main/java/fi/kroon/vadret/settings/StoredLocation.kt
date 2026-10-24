package fi.kroon.vadret.settings

/**
 *  The place searched for last, used while GPS is off.
 */
data class StoredLocation(
    val city: String,
    val municipality: String,
    val county: String,
    val latitude: String,
    val longitude: String
)