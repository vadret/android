package fi.kroon.vadret.data.theme.model

import androidx.annotation.StyleRes

/**
 * Styles are owned by the app, which provides them.
 */
data class ThemeStyles(
    @StyleRes
    val light: Int,
    @StyleRes
    val dark: Int,
    @StyleRes
    val amoled: Int
)