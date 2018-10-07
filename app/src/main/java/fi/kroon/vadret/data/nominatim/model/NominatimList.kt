package fi.kroon.vadret.data.nominatim.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NominatimList(
    val payload: List<Nominatim>
) : Parcelable