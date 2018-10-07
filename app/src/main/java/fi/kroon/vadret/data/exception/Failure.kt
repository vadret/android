package fi.kroon.vadret.data.exception

sealed class Failure {

    class IOException : Failure()
    class NetworkException : Failure()
    class NetworkOfflineFailure : Failure()

    class HttpBadRequest400 : Failure()
    class HttpInternalServerError500 : Failure()
    class HttpForbidden403 : Failure()
    class HttpServiceUnavailable503 : Failure()
    class HttpGatewayTimeout504 : Failure()

    /**
     * For feature specific failure
     * extend from FeatureFailure
     */

    abstract class FeatureFailure : Failure()
}