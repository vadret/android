package fi.kroon.vadret.data.weather.net

import fi.kroon.vadret.di.scope.VadretApplicationScope
import fi.kroon.vadret.utils.NetworkHandler
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@VadretApplicationScope
class WeatherOfflineInterceptor @Inject constructor(private val networkHandler: NetworkHandler) : Interceptor {
    companion object {
        const val DEFAULT_CACHE_REQUEST_TTL = 60
        const val DEFAULT_CACHE_RESPONSE_TTL = 60 * 60 * 24
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        /**
         *  Cache-Control: max-age=3600,public
         */

        var request = chain.request()
        request = if (networkHandler.isConnected!!) {
            Timber.d("Cache miss! Network is available tho.")
            request.newBuilder().header("Cache-Control", "public, max-age=$DEFAULT_CACHE_REQUEST_TTL").build()
        } else {
            Timber.d("Cache hit! no network")
            request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=$DEFAULT_CACHE_RESPONSE_TTL").build()
        }
        return chain.proceed(request)
    }
}