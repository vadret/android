package fi.kroon.vadret.utils.extensions

import fi.kroon.vadret.data.nominatim.model.Address
import fi.kroon.vadret.data.nominatim.model.Nominatim
import fi.kroon.vadret.data.nominatim.model.NominatimList

fun NominatimList.toFilteredNominatimArray() = this
    .payload.filter {
        nominatim -> toFilter(nominatim)
    }.map {
        it
    }.toTypedArray()

fun NominatimList.toFilteredStringArray() = this
    .payload.filter {
        nominatimString -> toFilter(nominatimString)
    }.map {
        it.displayName
    }.toTypedArray()

fun toFilter(nominatim: Nominatim): Boolean = nominatim.address?.let { hasRequiredAddress(it) && hasRequiredType(nominatim) } ?: false

fun hasRequiredType(nominatim: Nominatim): Boolean = when (nominatim.type) {
    "city", "hamlet", "village", "administrative", "town" -> true
    else -> false
}

fun hasRequiredAddress(address: Address): Boolean = when {
    address.city != null && address.state != null -> true
    address.hamlet != null && address.state != null -> true
    address.village != null && address.state != null -> true
    address.town != null && address.state != null -> true
    else -> false
}