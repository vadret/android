package fi.kroon.vadret.data.exception

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.di.scope.CoreApplicationScope
import fi.kroon.vadret.util.extension.empty
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@CoreApplicationScope
class ExceptionHandler @Inject constructor() : IExceptionHandler<Failure> {
    override fun invoke(throwable: Throwable): Failure =
        when (throwable) {
            is UnknownHostException -> Failure.NetworkDNSError()
            is SocketTimeoutException -> Failure.NetworkTimeOutError()
            else -> Failure.UnHandledError(message = throwable.message ?: String.empty())
        }
}