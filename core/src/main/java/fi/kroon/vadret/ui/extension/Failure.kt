package fi.kroon.vadret.ui.extension

import androidx.annotation.StringRes
import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.core.R

/**
 *  Several codes share a message, the user can act on them the same way.
 */
@StringRes
fun Failure.messageResourceId(): Int =
    when (errorCode) {
        ErrorCode.NETWORK_UNAVAILABLE -> R.string.no_network_available
        ErrorCode.NETWORK_FAILURE,
        ErrorCode.HTTP_NOT_FOUND,
        ErrorCode.HTTP_UNEXPECTED_STATUS,
        ErrorCode.INVALID_RESPONSE -> R.string.network_failure
        ErrorCode.HTTP_BAD_REQUEST -> R.string.http_bad_request_400
        ErrorCode.HTTP_FORBIDDEN -> R.string.http_forbidden_403
        ErrorCode.HTTP_INTERNAL_SERVER_ERROR -> R.string.http_internal_server_error_500
        ErrorCode.HTTP_SERVICE_UNAVAILABLE -> R.string.http_service_unavailable_503
        ErrorCode.HTTP_GATEWAY_TIMEOUT -> R.string.http_gateway_timeout_504
        ErrorCode.LOCATION_PROVIDER_DISABLED -> R.string.location_provider_disabled
        ErrorCode.LOCATION_NOT_AVAILABLE -> R.string.location_failure
        ErrorCode.LOCATION_PERMISSION_MISSING -> R.string.no_location_permission
        ErrorCode.FILE_READ_FAILED -> R.string.failed_loading_file
        ErrorCode.WEATHER_FORECAST_NOT_AVAILABLE_FOR_LOCATION -> R.string.data_not_available_at_this_location
        ErrorCode.RADAR_NOT_AVAILABLE -> R.string.no_radar_available
        ErrorCode.DISTRICTS_NOT_AVAILABLE -> R.string.filter_failure_districts_not_available
        ErrorCode.STORAGE_FAILED,
        ErrorCode.UNKNOWN -> R.string.unhandled_error
    }