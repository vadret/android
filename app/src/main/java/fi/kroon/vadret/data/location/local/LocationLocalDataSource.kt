package fi.kroon.vadret.data.location.local

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.location.exception.LocationFailure
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import timber.log.Timber
import javax.inject.Inject

class LocationLocalDataSource @Inject constructor(
    private val locationManager: LocationManager
) {

    private val isGPSEnabled: Boolean by lazy {
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private val isNetworkLocationProviderEnabled: Boolean by lazy {
        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    operator fun invoke(): Either<Failure, fi.kroon.vadret.data.location.model.Location> = when {
        (isGPSEnabled.not() && isNetworkLocationProviderEnabled.not()) -> {
            Timber.e("DisplayError: isGPSEnabled: $isGPSEnabled, isNetworkLocationProviderEnabled: $isNetworkLocationProviderEnabled")
            LocationFailure
                .LocationProviderDisabled
                .asLeft()
        }
        else ->
            getLocation(
                isNetworkLocationProviderEnabled,
                isGPSEnabled
            )?.let { position ->
                Timber.d("Position aquired: $position")
                fi.kroon.vadret.data.location.model.Location(
                    latitude = position.latitude,
                    longitude = position.longitude
                ).asRight()
            } ?: LocationFailure
                .LocationNotAvailable
                .asLeft()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(isNetworkLocationProviderEnabled: Boolean, isGpsEnabled: Boolean): android.location.Location? {
        return when {
            (isNetworkLocationProviderEnabled && isGpsEnabled) -> {
                Timber.d("Trying NETWORK_PROVIDER. If fails proceed with GPS_PROVIDER")
                return locationManager.getLastKnownLocation(NETWORK_PROVIDER)
                    ?: locationManager.getLastKnownLocation(GPS_PROVIDER)
            }
            isNetworkLocationProviderEnabled -> {
                Timber.d("Trying to call getLastKnownLocation() with NETWORK_PROVIDER")
                val location: Location? = locationManager.getLastKnownLocation(NETWORK_PROVIDER)
                Timber.d("LocationManager provided: $location")
                return location
            }
            isGpsEnabled -> {
                Timber.d("Trying to call getLastKnownLocation() with GPS_PROVIDER")
                val location: Location? = locationManager.getLastKnownLocation(GPS_PROVIDER)
                Timber.d("LocationManager provided: $location")
                return location
            }
            else -> {
                Timber.e("getLocation: null")
                null
            }
        }
    }
}