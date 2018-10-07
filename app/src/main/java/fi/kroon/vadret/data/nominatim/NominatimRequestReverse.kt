package fi.kroon.vadret.data.nominatim

import fi.kroon.vadret.data.DEFAULT_NOMATIM_FORMAT

class NominatimRequestReverse(
    val format: String = DEFAULT_NOMATIM_FORMAT,
    val latitude: Double,
    val longitude: Double,
    val zoom: Int = 16 // CITY LEVEL
)