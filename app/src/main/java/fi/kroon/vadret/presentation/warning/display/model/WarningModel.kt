package fi.kroon.vadret.presentation.warning.display.model

data class WarningModel(
    val identifier: String,
    val pushMessage: String,
    val updated: String,
    val published: String,
    val headline: String,
    val preamble: String,
    val bodyText: String,
    val areaList: List<AreaModel>,
    val web: String?,
    val language: String,
    val event: String,
    val senderName: String,
    val push: Boolean,
    val bodyLinks: List<String>,
    val sourceId: Int?,
    val isVma: Boolean,
    val isTestVma: Boolean,
    val backgroundResourceId: Int,
    val elapsedTime: ElapsedTime
) : IWarningModel