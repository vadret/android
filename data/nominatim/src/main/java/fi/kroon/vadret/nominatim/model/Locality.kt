package fi.kroon.vadret.nominatim.model

/**
 *  [name] is null when no city, hamlet or village lies at the place.
 */
data class Locality(val name: String?)