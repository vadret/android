package fi.kroon.vadret.data.location

import android.annotation.SuppressLint
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.location.exception.LocationFailure
import fi.kroon.vadret.data.location.model.Location
import timber.log.Timber
import javax.inject.Inject

class LocationProvider @Inject constructor(
    private val locationManager: LocationManager
) : LocationListener {
    companion object {
        const val MIN_TIME = 1000L * 60L
        const val MIN_DISTANCE = 100f
    }

    fun get(): Either<Failure, Location> {
        try {
            val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNlpEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            /**
             *  We support both NLP and GPS
             *  however only one is actually required
             *  to use the application.
             */

            logAvailableSource(isGPSEnabled, isNlpEnabled)

            if (!isGPSEnabled and !isNlpEnabled) {
                Timber.d("No location permissions available.")
                return Either.Left(LocationFailure.NoLocationPermissions())
            } else {
                val location = getLocation(isNlpEnabled, isGPSEnabled)

                /**
                 * if no recent location exists, we must handle it somehow.
                 */

                location?.let {
                    Timber.d("Location found lat:${it.latitude}  lon:${it.longitude}")
                    return with(it) {
                        Either.Right(Location(
                            latitude = latitude,
                            longitude = longitude
                        ))
                    }
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

        return Either.Left(LocationFailure.LocationNotAvailableFailure())
    }

    private fun getLocation(isNlpEnabled: Boolean, isGPSEnabled: Boolean): android.location.Location? {
        var location: android.location.Location? = null

        if (isNlpEnabled) {
            location = getLocationFromNLPProvider()
        }

        /**
         * If getting location from NLP failed, we can try to getLibraries it from GPS
         */
        if (location == null && isGPSEnabled) {
            location = getLocationFromGPSProvider()
        }

        return location
    }

    private fun getLocationFromGPSProvider(): android.location.Location? {
        Timber.d("Location manager fetching from GPS.")
        return getLocationFromProvider(LocationManager.GPS_PROVIDER)
    }

    private fun getLocationFromNLPProvider(): android.location.Location? {
        Timber.d("Location manager fetching from NLP.")
        return getLocationFromProvider(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    fun getLocationFromProvider(provider: String): android.location.Location? {
        locationManager.requestLocationUpdates(
            provider,
            MIN_TIME,
            MIN_DISTANCE,
            this
        )

        return locationManager.getLastKnownLocation(provider)
    }

    private fun logAvailableSource(isGPSEnabled: Boolean, isNlpEnabled: Boolean) {
        if (isGPSEnabled) {
            Timber.d("GPS permission is available.")
        }

        if (isNlpEnabled) {
            Timber.d("NLP permission is available.")
        }
    }

    override fun onLocationChanged(location: android.location.Location?) {}
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String?) {}
    override fun onProviderDisabled(provider: String?) {}
}