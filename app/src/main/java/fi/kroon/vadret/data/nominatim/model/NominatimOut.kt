package fi.kroon.vadret.data.nominatim.model

import fi.kroon.vadret.util.NOMINATIM_DATA_FORMAT
import fi.kroon.vadret.util.SWEDEN
import fi.kroon.vadret.util.GERMANY
import fi.kroon.vadret.util.NORWAY
import fi.kroon.vadret.util.POLAND
import fi.kroon.vadret.util.DENMARK
import fi.kroon.vadret.util.FINLAND

data class NominatimOut(
    val city: String,
    val format: String = NOMINATIM_DATA_FORMAT,
    val countrycodes: String = "$GERMANY,$SWEDEN,$NORWAY,$POLAND,$DENMARK,$FINLAND",
    val nameDetails: Int = 1,
    val addressDetails: Int = 0,
    val limit: Int = 5
)