package fi.kroon.vadret.testing

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import okio.Path
import okio.Path.Companion.toOkioPath
import java.io.File
import java.nio.file.Files

private const val DIRECTORY_PREFIX = "datastore-test"
private const val PREFERENCES_FILE_NAME = "test.preferences_pb"

fun testPreferencesDataStore(scope: CoroutineScope): DataStore<Preferences> {
    val directory: File = Files.createTempDirectory(DIRECTORY_PREFIX).toFile()
    directory.deleteOnExit()
    val file: Path = File(directory, PREFERENCES_FILE_NAME).toOkioPath()
    return PreferenceDataStoreFactory.createWithPath(
        scope = scope,
        produceFile = { file }
    )
}