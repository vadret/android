package fi.kroon.vadret.data.exception

sealed class Failure {

    class IOException: Failure()
    class NetworkException: Failure()
    class NetworkOfflineFailure: Failure()

    /**
     * For feature specific failure
     * extend from FeatureFailure
     */

    abstract class FeatureFailure : Failure()
}