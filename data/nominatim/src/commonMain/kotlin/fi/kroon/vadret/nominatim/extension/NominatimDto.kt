package fi.kroon.vadret.nominatim.extension

import fi.kroon.vadret.nominatim.model.Locality
import fi.kroon.vadret.nominatim.remote.NominatimDto

internal fun NominatimDto.toLocality(): Locality = Locality(name = address?.city ?: address?.hamlet ?: address?.village)