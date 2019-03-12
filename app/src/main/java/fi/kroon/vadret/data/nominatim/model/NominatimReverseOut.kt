package fi.kroon.vadret.data.nominatim.model

import fi.kroon.vadret.utils.NOMINATIM_DATA_FORMAT
import fi.kroon.vadret.utils.NOMINATIM_CITY_ZOOM_LEVEL

class NominatimReverseOut(
    val format: String = NOMINATIM_DATA_FORMAT,
    val latitude: Double,
    val longitude: Double,
    val zoom: Int = NOMINATIM_CITY_ZOOM_LEVEL
)