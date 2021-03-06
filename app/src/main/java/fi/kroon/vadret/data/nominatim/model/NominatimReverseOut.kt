package fi.kroon.vadret.data.nominatim.model

import fi.kroon.vadret.util.NOMINATIM_CITY_ZOOM_LEVEL
import fi.kroon.vadret.util.NOMINATIM_DATA_FORMAT

class NominatimReverseOut(
    val format: String = NOMINATIM_DATA_FORMAT,
    val latitude: Double,
    val longitude: Double,
    val zoom: Int = NOMINATIM_CITY_ZOOM_LEVEL
)