package fi.kroon.vadret.model

data class AppInfo(
    val applicationId: String,
    val versionName: String,
    val isDebug: Boolean,
    val isDeveloperBuild: Boolean
)