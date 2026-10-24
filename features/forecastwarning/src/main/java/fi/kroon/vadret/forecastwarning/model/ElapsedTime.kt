package fi.kroon.vadret.forecastwarning.model

import androidx.annotation.StringRes

/**
 *  How long ago a warning was published, shown as
 *  "[count] [resourceId]" when [count] is set.
 */
data class ElapsedTime(
    @StringRes
    val resourceId: Int,
    val count: Long? = null
)