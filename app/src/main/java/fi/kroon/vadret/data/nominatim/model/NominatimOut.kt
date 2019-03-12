package fi.kroon.vadret.data.nominatim.model

import fi.kroon.vadret.utils.NOMINATIM_DATA_FORMAT
import fi.kroon.vadret.utils.SWEDEN
import fi.kroon.vadret.utils.GERMANY
import fi.kroon.vadret.utils.NORWAY
import fi.kroon.vadret.utils.POLAND
import fi.kroon.vadret.utils.DENMARK
import fi.kroon.vadret.utils.FINLAND

data class NominatimOut(
    val city: String,
    val format: String = NOMINATIM_DATA_FORMAT,
    val countrycodes: String = "$GERMANY,$SWEDEN,$NORWAY,$POLAND,$DENMARK,$FINLAND",
    val nameDetails: Int = 1,
    val addressDetails: Int = 0,
    val limit: Int = 5
)