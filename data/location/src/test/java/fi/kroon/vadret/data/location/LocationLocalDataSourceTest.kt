package fi.kroon.vadret.data.location

import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.location.exception.LocationFailure
import fi.kroon.vadret.data.location.local.LocationLocalDataSource
import fi.kroon.vadret.data.location.model.Location
import fi.kroon.vadret.util.DEFAULT_LATITUDE
import fi.kroon.vadret.util.DEFAULT_LONGITUDE
import io.github.sphrak.either.Either
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import kotlin.test.Test

class LocationLocalDataSourceTest {

    private val mockAndroidLocation: android.location.Location = mock(android.location.Location::class.java)

    private val mockLocationManager: LocationManager = mock(LocationManager::class.java)

    private val mockLocationLocalDataSource: LocationLocalDataSource = LocationLocalDataSource(mockLocationManager)

    @Test
    fun `both nlp and gps are disabled returns failure`() {
        doReturn(false).`when`(mockLocationManager).isProviderEnabled(GPS_PROVIDER)
        doReturn(false).`when`(mockLocationManager).isProviderEnabled(NETWORK_PROVIDER)

        val result: Either<Failure, Location> = mockLocationLocalDataSource()

        assert(
            result is Either.Left<Failure> && result.a is LocationFailure.LocationProviderDisabled
        )
    }

    @Test
    fun `location provider returns location from network location provider`() {
        setupMockLocationReturns()
        doReturn(true).`when`(mockLocationManager).isProviderEnabled(NETWORK_PROVIDER)
        doReturn(mockAndroidLocation).`when`(mockLocationManager).getLastKnownLocation(NETWORK_PROVIDER)

        val result: Either<Failure, Location> = mockLocationLocalDataSource()
        assert(
            result is Either.Right &&
                result.b.latitude == mockAndroidLocation.latitude &&
                result.b.longitude == mockAndroidLocation.longitude
        )
    }

    @Test
    fun `location provider returns location from global positioning provider`() {
        setupMockLocationReturns()
        doReturn(false).`when`(mockLocationManager).isProviderEnabled(NETWORK_PROVIDER)
        doReturn(true).`when`(mockLocationManager).isProviderEnabled(GPS_PROVIDER)
        doReturn(mockAndroidLocation).`when`(mockLocationManager).getLastKnownLocation(GPS_PROVIDER)

        val result: Either<Failure, Location> = mockLocationLocalDataSource()
        assert(
            result is Either.Right &&
                result.b.latitude == mockAndroidLocation.latitude &&
                result.b.longitude == mockAndroidLocation.longitude
        )
    }

    @Test
    fun `location provider fails because null was recieved`() {
        doReturn(true).`when`(mockLocationManager).isProviderEnabled(GPS_PROVIDER)
        doReturn(true).`when`(mockLocationManager).isProviderEnabled(NETWORK_PROVIDER)
        doReturn(null).`when`(mockLocationManager).getLastKnownLocation(NETWORK_PROVIDER)

        val result: Either<Failure, Location> = mockLocationLocalDataSource()
        assert(
            result is Either.Left<Failure> && result.a is LocationFailure.LocationNotAvailable
        )
    }

    /*@Test
    fun `throw security exception on anyString`() {
        doThrow(SecurityException()).`when`(mockLocationManager).isProviderEnabled(anyString())
        val result = mockLocationLocalDataSource()
        assert(
            result is Either.Left<Failure> && result.a is LocationFailure.LocationNotAvailable
        )
    }*/

    private fun setupMockLocationReturns() {
        doReturn(DEFAULT_LONGITUDE.toDouble()).`when`(mockAndroidLocation).longitude
        doReturn(DEFAULT_LATITUDE.toDouble()).`when`(mockAndroidLocation).latitude
    }
}