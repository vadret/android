package fi.kroon.vadret.location.local

import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

private const val AUTOMATIC_KEY = "AUTOMATIC_LOCATION_MODE"
private const val LOCALITY_KEY = "LOCALITY"
private const val MUNICIPALITY_KEY = "MUNICIPALITY"
private const val COUNTY_KEY = "COUNTY"
private const val LATITUDE_KEY = "LATITUDE"
private const val LONGITUDE_KEY = "LONGITUDE"

/**
 *  Until the user picks a place the forecast follows the
 *  device, and falls back to Stockholm without location access.
 */
private const val DEFAULT_IS_AUTOMATIC = true
private const val DEFAULT_LOCALITY = "Stockholm"
private const val DEFAULT_MUNICIPALITY = "Stockholm"
private const val DEFAULT_COUNTY = "Stockholm"
private const val DEFAULT_LATITUDE = "59.3293"
private const val DEFAULT_LONGITUDE = "18.0686"

internal class DefaultStoredLocationLocalDataSource(
    private val sharedPreferences: SharedPreferences,
    private val coroutineDispatcher: CoroutineDispatcher
) : StoredLocationLocalDataSource {

    override suspend fun isAutomatic(): Boolean =
        withContext(context = coroutineDispatcher) {
            sharedPreferences.getBoolean(AUTOMATIC_KEY, DEFAULT_IS_AUTOMATIC)
        }

    override suspend fun setAutomatic(isAutomatic: Boolean) {
        withContext(context = coroutineDispatcher) {
            sharedPreferences
                .edit()
                .putBoolean(AUTOMATIC_KEY, isAutomatic)
                .apply()
        }
    }

    override suspend fun getStoredLocation(): StoredLocationEntity =
        withContext(context = coroutineDispatcher) {
            StoredLocationEntity(
                locality = readString(
                    key = LOCALITY_KEY,
                    defaultValue = DEFAULT_LOCALITY
                ),
                municipality = readString(
                    key = MUNICIPALITY_KEY,
                    defaultValue = DEFAULT_MUNICIPALITY
                ),
                county = readString(
                    key = COUNTY_KEY,
                    defaultValue = DEFAULT_COUNTY
                ),
                latitude = readString(
                    key = LATITUDE_KEY,
                    defaultValue = DEFAULT_LATITUDE
                ),
                longitude = readString(
                    key = LONGITUDE_KEY,
                    defaultValue = DEFAULT_LONGITUDE
                )
            )
        }

    /**
     *  One edit, so the place is never stored half way.
     */
    override suspend fun setStoredLocation(storedLocationEntity: StoredLocationEntity) {
        withContext(context = coroutineDispatcher) {
            sharedPreferences
                .edit()
                .putString(LOCALITY_KEY, storedLocationEntity.locality)
                .putString(MUNICIPALITY_KEY, storedLocationEntity.municipality)
                .putString(COUNTY_KEY, storedLocationEntity.county)
                .putString(LATITUDE_KEY, storedLocationEntity.latitude)
                .putString(LONGITUDE_KEY, storedLocationEntity.longitude)
                .apply()
        }
    }

    private fun readString(
        key: String,
        defaultValue: String
    ): String =
        sharedPreferences.getString(key, defaultValue) ?: defaultValue
}