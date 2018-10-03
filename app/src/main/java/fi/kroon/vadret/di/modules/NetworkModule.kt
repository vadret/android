package fi.kroon.vadret.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.weather.net.CacheInterceptor
import fi.kroon.vadret.di.scope.VadretApplicationScope
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

@Module(includes = [ApplicationModule::class])
class NetworkModule {

    @Module
    companion object {
        private const val DEFAULT_CONNECTION_TIMEOUT = 10000L
        private const val READ_CONNECTION_TIMEOUT = 10000L
        private const val WRITE_CONNECTION_TIMEOUT = 10000L
        private const val TEN_MB_CACHE_IN_BYTES = 10L * 1024L * 1024L

        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun cache(context: Context): Cache {
            return Cache(context.cacheDir, TEN_MB_CACHE_IN_BYTES)
        }

        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun httpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun okHttpClient(
            cache: Cache,
            cacheInterceptor: CacheInterceptor,
            httpLoggingInterceptor: HttpLoggingInterceptor
        ): OkHttpClient {
            return OkHttpClient.Builder()
                .connectTimeout(DEFAULT_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(READ_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(WRITE_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .cache(cache)
                .addInterceptor(cacheInterceptor)
                .addInterceptor(httpLoggingInterceptor)
                .build()
        }
    }
}