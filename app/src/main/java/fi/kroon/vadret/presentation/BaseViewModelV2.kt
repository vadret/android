package fi.kroon.vadret.presentation

import fi.kroon.vadret.R
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.location.exception.LocationFailure
import fi.kroon.vadret.data.nominatim.exception.NominatimFailure
import fi.kroon.vadret.data.weather.exception.WeatherForecastFailure

abstract class BaseViewModelV2 {

    protected fun getErrorCode(failure: Failure): Int =
        when (failure) {
            is Failure.HttpBadRequest400 -> R.string.http_bad_request_400
            is Failure.HttpForbidden403 -> R.string.http_forbidden_403
            is Failure.HttpGatewayTimeout504 -> R.string.http_gateway_timeout_504
            is Failure.HttpInternalServerError500 -> R.string.http_internal_server_error_500
            is Failure.HttpServiceUnavailable503 -> R.string.http_service_unavailable_503
            is Failure.IOException -> R.string.io_exception
            is Failure.NetworkException -> R.string.network_failure
            is Failure.NetworkOfflineFailure -> R.string.no_network_available
            is LocationFailure.LocationNotAvailable -> R.string.location_failure
            is LocationFailure.NoLocationPermissions -> R.string.no_location_permission
            is NominatimFailure.NominatimNotAvailable -> R.string.search_failed
            is WeatherForecastFailure.NoWeatherAvailable -> R.string.no_weather_available
            is Failure.DiskCacheEvictionFailure -> R.string.disk_cache_evict
            is Failure.DiskCacheLruReadFailure -> R.string.disk_cache_read
            is Failure.DiskCacheLruWriteFailure -> R.string.disk_cache_write
            is WeatherForecastFailure.NoWeatherAvailableForThisLocation -> R.string.data_not_available_at_this_location
            else -> R.string.unhandled_error
        }
}