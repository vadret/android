package fi.kroon.vadret.data.theme.model

import androidx.annotation.StyleRes

data class Theme(
    val name: String,
    @StyleRes
    val resourceId: Int
)