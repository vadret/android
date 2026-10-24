package fi.kroon.vadret.testing

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import okio.Path
import okio.Path.Companion.toOkioPath
import java.io.File
import java.nio.file.Files

fun testPreferencesDataStore(scope: CoroutineScope): DataStore<Preferences> {
    val directory: File = Files.createTempDirectory("datastore-test").toFile()
    directory.deleteOnExit()
    val file: Path = File(directory, "test.preferences_pb").toOkioPath()
    return PreferenceDataStoreFactory.createWithPath(
        scope = scope,
        produceFile = { file }
    )
}