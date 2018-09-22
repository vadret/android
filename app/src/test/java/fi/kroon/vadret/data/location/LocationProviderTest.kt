package fi.kroon.vadret.data.location

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.location.exception.LocationFailure
import fi.kroon.vadret.util.anyObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.doThrow
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LocationProviderTest {

    @Mock
    lateinit var mockLocationManager: LocationManager

    @Mock
    lateinit var mockLocation: Location

    private lateinit var testLocationProvider: LocationProvider

    @Before
    fun setup() {
        testLocationProvider = LocationProvider(mockLocationManager)
    }

    @Test
    fun noProviderEnabled_shouldReturnNoLocationPermissionsFailure() {
        doReturn(false).`when`(mockLocationManager).isProviderEnabled(GPS_PROVIDER)
        doReturn(false).`when`(mockLocationManager).isProviderEnabled(NETWORK_PROVIDER)

        val result = testLocationProvider.get()

        assert(result is Either.Left<Failure> && result.a is LocationFailure.NoLocationPermissions)
    }

    @Test
    fun noLastKnownLocationAvailable_shouldReturnLocationNotAvailableFailure() {
        doReturn(true).`when`(mockLocationManager).isProviderEnabled(GPS_PROVIDER)
        doReturn(true).`when`(mockLocationManager).isProviderEnabled(NETWORK_PROVIDER)
        doReturn(null).`when`(mockLocationManager).getLastKnownLocation(GPS_PROVIDER)
        doReturn(null).`when`(mockLocationManager).getLastKnownLocation(NETWORK_PROVIDER)
        doNothing().`when`(mockLocationManager).requestLocationUpdates(anyString(), anyLong(), anyFloat(), anyObject<LocationListener>())

        val result = testLocationProvider.get()

        assert(result is Either.Left<Failure> && result.a is LocationFailure.LocationNotAvailableFailure)
    }

    @Test
    fun exceptionThrown_shouldReturnLocationNotAvailableFailure() {
        doThrow(SecurityException()).`when`(mockLocationManager).isProviderEnabled(anyString())

        val result = testLocationProvider.get()

        assert(result is Either.Left<Failure> && result.a is LocationFailure.LocationNotAvailableFailure)
    }

    @Test
    fun gpsLocationReturned_shouldReturnLocationEither() {
        setupMockLocationReturns()
        doReturn(false).`when`(mockLocationManager).isProviderEnabled(NETWORK_PROVIDER)
        doReturn(true).`when`(mockLocationManager).isProviderEnabled(GPS_PROVIDER)
        doReturn(mockLocation).`when`(mockLocationManager).getLastKnownLocation(GPS_PROVIDER)
        doNothing().`when`(mockLocationManager).requestLocationUpdates(anyString(), anyLong(), anyFloat(), anyObject<LocationListener>())

        val result = testLocationProvider.get()

        assert(result is Either.Right &&
            result.b.latitude == mockLocation.latitude &&
            result.b.longitude == mockLocation.longitude)
    }

    @Test
    fun nlpLocationReturned_shouldReturnLocationEither() {
        setupMockLocationReturns()
        doReturn(true).`when`(mockLocationManager).isProviderEnabled(NETWORK_PROVIDER)
        doReturn(false).`when`(mockLocationManager).isProviderEnabled(GPS_PROVIDER)
        doReturn(mockLocation).`when`(mockLocationManager).getLastKnownLocation(NETWORK_PROVIDER)
        doNothing().`when`(mockLocationManager).requestLocationUpdates(anyString(), anyLong(), anyFloat(), anyObject<LocationListener>())

        val result = testLocationProvider.get()

        assert(result is Either.Right &&
            result.b.latitude == mockLocation.latitude &&
            result.b.longitude == mockLocation.longitude)
    }

    @Test
    fun nlpReturnsNullGpsReturnsLocation_shouldReturnLocationEither() {
        setupMockLocationReturns()
        doReturn(true).`when`(mockLocationManager).isProviderEnabled(NETWORK_PROVIDER)
        doReturn(true).`when`(mockLocationManager).isProviderEnabled(GPS_PROVIDER)
        doReturn(null).`when`(mockLocationManager).getLastKnownLocation(NETWORK_PROVIDER)
        doReturn(mockLocation).`when`(mockLocationManager).getLastKnownLocation(GPS_PROVIDER)
        doNothing().`when`(mockLocationManager).requestLocationUpdates(anyString(), anyLong(), anyFloat(), anyObject<LocationListener>())

        val result = testLocationProvider.get()

        assert(result is Either.Right &&
            result.b.latitude == mockLocation.latitude &&
            result.b.longitude == mockLocation.longitude)
    }

    private fun setupMockLocationReturns() {
        doReturn(12.694513).`when`(mockLocation).longitude
        doReturn(56.046468).`when`(mockLocation).latitude
    }
}