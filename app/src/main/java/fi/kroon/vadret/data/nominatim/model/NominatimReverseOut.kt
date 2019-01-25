package fi.kroon.vadret.data.nominatim.model

import fi.kroon.vadret.utils.DEFAULT_NOMATIM_FORMAT
import fi.kroon.vadret.utils.DEFAULT_NOMINATIM_ZOOM_LEVEL

class NominatimReverseOut(
    val format: String = DEFAULT_NOMATIM_FORMAT,
    val latitude: Double,
    val longitude: Double,
    val zoom: Int = DEFAULT_NOMINATIM_ZOOM_LEVEL
)