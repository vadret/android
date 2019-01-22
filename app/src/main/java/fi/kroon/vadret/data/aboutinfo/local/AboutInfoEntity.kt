package fi.kroon.vadret.data.aboutinfo.local

data class AboutInfoEntity(
    val iconResourceId: Int?,
    val titleResourceId: Int?,
    val urlResourceId: Int? = null,
    val hintResourceId: Int? = null
)