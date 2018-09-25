package fi.kroon.vadret.presentation.common.model

data class BaseRowModel(
    val iconResId: Int?,
    val titleResId: Int?,
    val hintResId: Int? = null,
    val urlResId: Int? = null
)