package fi.kroon.vadret.changelog.local

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream

internal class DefaultChangelogLocalDataSource(
    private val openChangelog: () -> InputStream,
    private val coroutineDispatcher: CoroutineDispatcher
) : ChangelogLocalDataSource {

    override suspend fun getChangelog(): String =
        withContext(context = coroutineDispatcher) {
            openChangelog().bufferedReader().use { reader: BufferedReader -> reader.readText() }
        }
}