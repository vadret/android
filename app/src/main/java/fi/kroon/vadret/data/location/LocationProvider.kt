package fi.kroon.vadret.data.location

import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import javax.inject.Inject

const val TAG = "LocationProvider"

class LocationProvider @Inject constructor(
    private val context: Context
) : LocationListener {

    fun getLocation(): Either<Failure, Location> {

        try {
            val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNlpEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            /**
             *  We support both NLP and GPS
             *  however only one is actually required
             *  to use the application.
             */

            if (isGPSEnabled) {
                Log.d(TAG, "GPS permission is available.")
            }

            if (isNlpEnabled) {
                Log.d(TAG, "NLP permission is available.")
            }

            if (!isGPSEnabled and !isNlpEnabled) {
                Log.d(TAG, "No location permissions available.")
                return Either.Left(LocationFailure.NoLocationPermissions())
            } else {
                if (isNlpEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            1000L * 60L,
                            100f,
                            this
                    )
                    Log.d(TAG, "Location manager fetching from NLP.")

                    /**
                     * if no recent location exists, we must handle it somehow.
                     */
                    val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    val b = with(location) {
                        Location(
                                latitude = latitude,
                                longitude = longitude
                        )
                    }
                    return Either.Right(b)
                }

                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            1000L * 60,
                            100f,
                            this
                    )

                    val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    val b = with(location) { Location(latitude = latitude, longitude = longitude) }
                    return Either.Right(b)
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
        return Either.Left(LocationFailure.LocationFailure())
    }

    override fun onLocationChanged(location: android.location.Location?) {}
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String?) {}
    override fun onProviderDisabled(provider: String?) {}
}