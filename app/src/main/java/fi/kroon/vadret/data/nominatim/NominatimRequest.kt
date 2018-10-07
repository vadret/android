package fi.kroon.vadret.data.nominatim

import fi.kroon.vadret.data.DEFAULT_NOMATIM_FORMAT
import fi.kroon.vadret.data.SWEDEN
import fi.kroon.vadret.data.GERMANY
import fi.kroon.vadret.data.NORWAY
import fi.kroon.vadret.data.POLAND
import fi.kroon.vadret.data.DENMARK
import fi.kroon.vadret.data.FINLAND

data class NominatimRequest(
    val city: String,
    val format: String = DEFAULT_NOMATIM_FORMAT,
    val countrycodes: String = "$GERMANY,$SWEDEN,$NORWAY,$POLAND,$DENMARK,$FINLAND",
    val nameDetails: Int = 1,
    val addressDetails: Int = 0,
    val limit: Int = 5
)