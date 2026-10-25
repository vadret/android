package fi.kroon.vadret.ui.model

import androidx.annotation.StringRes

data class ElapsedTime(
    @StringRes
    val resourceId: Int,
    val count: Long? = null
)