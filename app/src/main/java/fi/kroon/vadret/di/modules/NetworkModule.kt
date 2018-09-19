package fi.kroon.vadret.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.weather.net.WeatherOfflineInterceptor
import fi.kroon.vadret.di.scope.VadretApplicationScope
import fi.kroon.vadret.utils.NetworkHandler
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit

@Module(includes = [ApplicationModule::class])
class NetworkModule {


    @Module
    companion object {

        const val DEFAULT_CONNECTION_TIMEOUT = 10000L
        const val READ_CONNECTION_TIMEOUT = 10000L
        const val WRITE_CONNECTION_TIMEOUT = 10000L

        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun cache(file: File): Cache {
            return Cache(file, (10L * 1024L * 1024L))
        }

        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun file(context: Context): File {
            return File(context.cacheDir, "okhttp_cache")
        }

        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun httpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun weatherOfflineInterceptor(networkHandler: NetworkHandler): WeatherOfflineInterceptor = WeatherOfflineInterceptor(networkHandler)

        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun okHttpClient(
                cache: Cache,
                weatherOfflineInterceptor: WeatherOfflineInterceptor,
                httpLoggingInterceptor: HttpLoggingInterceptor
        ): OkHttpClient {
            return OkHttpClient.Builder()
                    .connectTimeout(DEFAULT_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                    .readTimeout(READ_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(WRITE_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                    .retryOnConnectionFailure(true)
                    .addInterceptor(weatherOfflineInterceptor)
                    .addInterceptor(httpLoggingInterceptor)
                    .cache(cache).build()
        }
    }
}