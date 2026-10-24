package fi.kroon.vadret.data.weatherforecast.model

import android.os.Parcelable
import fi.kroon.vadret.util.POINT_GEOTYPE
import fi.kroon.vadret.util.SNOW1G_API_VERSION
import fi.kroon.vadret.util.SNOW1G_CATEGORY
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherOut(
    val localityName: String? = null,
    val version: Int = SNOW1G_API_VERSION,
    val category: String = SNOW1G_CATEGORY,
    val geotype: String = POINT_GEOTYPE,
    val longitude: Double,
    val latitude: Double
) : Parcelable