package fi.kroon.vadret.location.local

import fi.kroon.vadret.testing.testPreferencesDataStore
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultStoredLocationLocalDataSourceTest {

    @Test
    fun `nothing stored follows the device and falls back to Stockholm`() = runTest {
        val storedLocationLocalDataSource: StoredLocationLocalDataSource = DefaultStoredLocationLocalDataSource(
            dataStore = testPreferencesDataStore(scope = backgroundScope)
        )

        assertTrue(actual = storedLocationLocalDataSource.isAutomatic())
        assertEquals(
            expected = StoredLocationEntity(
                locality = "Stockholm",
                municipality = "Stockholm",
                county = "Stockholm",
                latitude = 59.3293,
                longitude = 18.0686
            ),
            actual = storedLocationLocalDataSource.getStoredLocation()
        )
    }

    @Test
    fun `a stored location and mode read back`() = runTest {
        val storedLocationLocalDataSource: StoredLocationLocalDataSource = DefaultStoredLocationLocalDataSource(
            dataStore = testPreferencesDataStore(scope = backgroundScope)
        )
        val storedLocationEntity = StoredLocationEntity(
            locality = "Umeå",
            municipality = "Umeå",
            county = "Västerbotten",
            latitude = 63.8258,
            longitude = 20.263
        )

        storedLocationLocalDataSource.setStoredLocation(storedLocationEntity = storedLocationEntity)
        storedLocationLocalDataSource.setAutomatic(isAutomatic = false)

        assertEquals(expected = storedLocationEntity, actual = storedLocationLocalDataSource.getStoredLocation())
        assertFalse(actual = storedLocationLocalDataSource.isAutomatic())
    }
}