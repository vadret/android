package fi.kroon.vadret.forecastwarning.model

import androidx.annotation.StringRes

data class ElapsedTime(
    @StringRes
    val resourceId: Int,
    val count: Long? = null
)