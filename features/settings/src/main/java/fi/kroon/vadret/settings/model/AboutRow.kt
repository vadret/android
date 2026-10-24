package fi.kroon.vadret.settings.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 *  A row on the about page that opens [urlResourceId].
 */
data class AboutRow(
    @DrawableRes
    val iconResourceId: Int,
    @StringRes
    val titleResourceId: Int,
    @StringRes
    val hintResourceId: Int?,
    @StringRes
    val urlResourceId: Int
)