package fi.kroon.vadret.data.weatherforecast.model

import android.os.Parcelable
import fi.kroon.vadret.utils.PMP3G_CATEGORY
import fi.kroon.vadret.utils.POINT_GEOTYPE
import fi.kroon.vadret.utils.SMHI_BASE_API_VERSION
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WeatherOut(
    val localityName: String? = null,
    val version: Int = SMHI_BASE_API_VERSION,
    val category: String = PMP3G_CATEGORY,
    val geotype: String = POINT_GEOTYPE,
    val longitude: Double,
    val latitude: Double
) : Parcelable