package fi.kroon.vadret.presentation.shared

import fi.kroon.vadret.R
import fi.kroon.vadret.data.aggregatedfeed.exception.AggregatedFeedFailure
import fi.kroon.vadret.data.common.exception.LocalFileReaderFailure
import fi.kroon.vadret.data.district.exception.DistrictFailure
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.location.exception.LocationFailure
import fi.kroon.vadret.data.nominatim.exception.NominatimFailure
import fi.kroon.vadret.data.radar.exception.RadarFailure
import fi.kroon.vadret.data.weatherforecast.exception.WeatherForecastFailure
import timber.log.Timber

interface IViewModel {

    val currentTimeMillis: Long
        get() = System.currentTimeMillis()

    fun getErrorCode(failure: Failure): Int =
        when (failure) {
            Failure.HttpBadRequest400 -> R.string.http_bad_request_400
            Failure.HttpForbidden403 -> R.string.http_forbidden_403
            Failure.HttpGatewayTimeout504 -> R.string.http_gateway_timeout_504
            Failure.HttpInternalServerError500 -> R.string.http_internal_server_error_500
            Failure.HttpServiceUnavailable503 -> R.string.http_service_unavailable_503
            Failure.IOException -> R.string.io_exception
            is Failure.NetworkError -> R.string.network_failure
            is Failure.NetworkOfflineError -> R.string.no_network_available
            RadarFailure.NoRadarAvailable -> R.string.no_radar_available
            LocationFailure.LocationNotAvailable -> R.string.location_failure
            LocationFailure.NoLocationPermissions -> R.string.no_location_permission
            LocationFailure.LocationProviderDisabled -> R.string.location_provider_disabled
            LocalFileReaderFailure.ReadFailure -> R.string.failed_loading_file
            NominatimFailure.NominatimNotAvailable -> R.string.search_failed
            Failure.DiskCacheEvictionFailure -> R.string.disk_cache_evict
            Failure.DiskCacheLruReadFailure -> R.string.disk_cache_read
            Failure.DiskCacheLruWriteFailure -> R.string.disk_cache_write
            WeatherForecastFailure.NoWeatherAvailableForThisLocation -> R.string.data_not_available_at_this_location
            WeatherForecastFailure.NoWeatherAvailable -> R.string.no_weather_available
            AggregatedFeedFailure.NoAggregatedFeedAvailable -> R.string.no_alert_available
            DistrictFailure.DistrictNotAvailable -> R.string.filter_failure_districts_not_available
            else -> {
                Timber.e("Error occured but was not properly handled: $failure")
                R.string.unhandled_error
            }
        }
}