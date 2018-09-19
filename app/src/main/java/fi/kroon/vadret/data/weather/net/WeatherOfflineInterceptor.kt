package fi.kroon.vadret.data.weather.net

import android.util.Log
import fi.kroon.vadret.di.scope.VadretApplicationScope
import fi.kroon.vadret.utils.NetworkHandler
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

const val TAG = "Cache intercept"

@VadretApplicationScope
class WeatherOfflineInterceptor @Inject constructor(private val networkHandler: NetworkHandler) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        /**
         *  Cache-Control: max-age=3600,public
         */

        val DEFAULT_CACHE_REQUEST_TTL = 60
        val DEFAULT_CACHE_RESPONSE_TTL = 60 * 60 * 24

        var request = chain.request()
        if (networkHandler.isConnected!!) {
            Log.d(TAG, "Cache miss! Network is available tho.")
            request = request.newBuilder().header("Cache-Control", "public, max-age=" + DEFAULT_CACHE_REQUEST_TTL).build()
        } else {
            Log.d(TAG, "Cache hit! no network")
            request = request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + DEFAULT_CACHE_RESPONSE_TTL).build()
        }
        return chain.proceed(request)
    }
}