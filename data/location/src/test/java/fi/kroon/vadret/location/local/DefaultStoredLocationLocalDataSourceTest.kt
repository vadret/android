package fi.kroon.vadret.location.local

import fi.kroon.vadret.testing.FakeSharedPreferences
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultStoredLocationLocalDataSourceTest {

    private val testScope = TestScope(StandardTestDispatcher())
    private val sharedPreferences = FakeSharedPreferences()
    private val storedLocationLocalDataSource: StoredLocationLocalDataSource = DefaultStoredLocationLocalDataSource(
        sharedPreferences = sharedPreferences,
        coroutineDispatcher = StandardTestDispatcher(testScope.testScheduler)
    )

    @Test
    fun `nothing stored follows the device and falls back to Stockholm`() = testScope.runTest {
        assertTrue(actual = storedLocationLocalDataSource.isAutomatic())
        assertEquals(
            expected = StoredLocationEntity(
                locality = "Stockholm",
                municipality = "Stockholm",
                county = "Stockholm",
                latitude = "59.3293",
                longitude = "18.0686"
            ),
            actual = storedLocationLocalDataSource.getStoredLocation()
        )
    }

    @Test
    fun `values stored by the old app are read`() = testScope.runTest {
        sharedPreferences.edit()
            .putBoolean("AUTOMATIC_LOCATION_MODE", false)
            .putString("LOCALITY", "Visby")
            .putString("MUNICIPALITY", "Gotland")
            .putString("COUNTY", "Gotland")
            .putString("LATITUDE", "57.6348")
            .putString("LONGITUDE", "18.2948")
            .apply()

        assertFalse(actual = storedLocationLocalDataSource.isAutomatic())
        assertEquals(
            expected = StoredLocationEntity(
                locality = "Visby",
                municipality = "Gotland",
                county = "Gotland",
                latitude = "57.6348",
                longitude = "18.2948"
            ),
            actual = storedLocationLocalDataSource.getStoredLocation()
        )
    }

    @Test
    fun `a stored location and mode read back`() = testScope.runTest {
        val storedLocationEntity = StoredLocationEntity(
            locality = "Umeå",
            municipality = "Umeå",
            county = "Västerbotten",
            latitude = "63.8258",
            longitude = "20.263"
        )

        storedLocationLocalDataSource.setStoredLocation(storedLocationEntity = storedLocationEntity)
        storedLocationLocalDataSource.setAutomatic(isAutomatic = false)

        assertEquals(expected = storedLocationEntity, actual = storedLocationLocalDataSource.getStoredLocation())
        assertFalse(actual = storedLocationLocalDataSource.isAutomatic())
    }
}