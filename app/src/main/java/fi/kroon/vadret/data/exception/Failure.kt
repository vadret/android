package fi.kroon.vadret.data.exception

sealed class Failure {

    object IOException : Failure()
    object NetworkException : Failure()
    object NetworkOfflineFailure : Failure()
    object NetworkResponseEmpty : Failure()
    object MemoryCacheLruWriteFailure : Failure()
    object MemoryCacheLruReadFailure : Failure()
    object MemoryCacheEvictionFailure : Failure()
    object DiskCacheLruWriteFailure : Failure()
    object DiskCacheLruReadFailure : Failure()
    object DiskCacheEvictionFailure : Failure()

    object HttpNotModified304 : Failure()
    object HttpBadRequest400 : Failure()
    object HttpForbidden403 : Failure()
    object HttpNotFound404 : Failure()
    object HttpInternalServerError500 : Failure()
    object HttpServiceUnavailable503 : Failure()
    object HttpGatewayTimeout504 : Failure()

    /**
     * For feature specific left
     * extend from FeatureFailure
     */
    abstract class FeatureFailure : Failure()
}