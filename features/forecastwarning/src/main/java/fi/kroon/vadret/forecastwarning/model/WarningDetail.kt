package fi.kroon.vadret.forecastwarning.model

import androidx.annotation.StringRes

sealed interface WarningDetail {

    val text: String

    data class Labelled(
        @StringRes
        val labelResourceId: Int,
        override val text: String
    ) : WarningDetail

    data class Titled(
        val title: String,
        override val text: String
    ) : WarningDetail

    data class Paragraph(
        override val text: String
    ) : WarningDetail
}