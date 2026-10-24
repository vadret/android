package fi.kroon.vadret.forecastwarning.model

data class WarningItem(
    val identifier: String,
    val headline: String,
    val bodyText: String,
    val preamble: String,
    val source: WarningSource,
    val elapsedTime: ElapsedTime
)