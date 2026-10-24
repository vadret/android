package fi.kroon.vadret.location.local

/**
 *  The coordinates are stored as text, as the old app did.
 */
internal data class StoredLocationEntity(
    val locality: String,
    val municipality: String,
    val county: String,
    val latitude: String,
    val longitude: String
)