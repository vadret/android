package fi.kroon.vadret.data.nominatim.model

import android.os.Parcelable
import androidx.annotation.StringRes
import fi.kroon.vadret.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class Locality(
    val name: String? = null,
    @StringRes
    val fallback: Int = R.string.unknown_area
) : Parcelable