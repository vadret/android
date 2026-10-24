package fi.kroon.vadret.data.aboutinfo.model

data class AboutInfo(
    val iconResourceId: Int?,
    val titleResourceId: Int?,
    val urlResourceId: Int? = null,
    val hintResourceId: Int? = null
)