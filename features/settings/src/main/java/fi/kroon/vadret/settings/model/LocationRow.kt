package fi.kroon.vadret.settings.model

import androidx.annotation.StringRes

internal data class LocationRow(
    @StringRes
    val titleResourceId: Int,
    val value: String
)