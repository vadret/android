package fi.kroon.vadret.changelog.local

internal interface ChangelogLocalDataSource {
    suspend fun getChangelog(): String
}