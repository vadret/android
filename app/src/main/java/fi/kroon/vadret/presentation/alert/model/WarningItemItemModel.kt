package fi.kroon.vadret.presentation.alert.model

data class WarningItemItemModel(
    val backgroundColorResource: Int,
    val diffInMillis: Long,
    val issuedAtStringResource: Int,
    val description: String,
    val eventLevelTitle: String,
    val headlineItems: List<String>,
    override val timeStamp: String
) : BaseWarningItemModel()