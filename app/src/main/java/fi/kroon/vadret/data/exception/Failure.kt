package fi.kroon.vadret.data.exception

import fi.kroon.vadret.util.extension.empty

sealed class Failure {

    object IOException : Failure()

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

    data class LocalKeyValueWriteError(val message: String = String.empty()) : Failure()
    data class LocalKeyValueReadError(val message: String = String.empty()) : Failure()

    data class CacheWriteError(val message: String = String.empty()) : Failure()
    data class CacheReadError(val message: String = String.empty()) : Failure()

    data class NetworkError(val message: String = String.empty()) : Failure()
    data class NetworkOfflineError(val message: String = String.empty()) : Failure()

    /**
     * For feature specific left
     * extend from FeatureFailure
     */
    abstract class FeatureFailure : Failure()
}