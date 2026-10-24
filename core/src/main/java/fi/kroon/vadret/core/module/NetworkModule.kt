package fi.kroon.vadret.core.module

import android.content.Context
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.core.BuildConfig
import fi.kroon.vadret.core.CoreScope
import fi.kroon.vadret.data.common.defaultJson
import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.util.extension.assertNoInitMainThread
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier

@Module
object NetworkModule {

    private const val DEFAULT_CONNECTION_TIMEOUT = 10000L
    private const val READ_CONNECTION_TIMEOUT = 10000L
    private const val WRITE_CONNECTION_TIMEOUT = 10000L
    private const val DEFAULT_CACHE_SIZE: Long = 33_554_432L
    private const val CACHE_DIRECTORY = "okhttp_cache"

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    private annotation class InternalApi

    private val getLogLevel: HttpLoggingInterceptor.Level =
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }

    @Provides
    @CoreScope
    fun provideFailureHandler(): ErrorHandler = ErrorHandler()

    @Provides
    @CoreScope
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor()
            .apply { level = getLogLevel }

    @Provides
    @InternalApi
    @CoreScope
    fun provideCache(
        context: Context
    ): Cache = Cache(
        directory = File(
            context.applicationContext.cacheDir,
            CACHE_DIRECTORY
        ),
        maxSize = DEFAULT_CACHE_SIZE
    )

    /**
     *  Nominatim rejects the stock okhttp user agent with 403,
     *  see https://operations.osmfoundation.org/policies/nominatim/
     */
    @Provides
    @InternalApi
    @CoreScope
    fun provideUserAgent(
        context: Context
    ): String {
        val versionName: String? = context.packageManager
            .getPackageInfo(context.packageName, 0)
            .versionName
        return "${context.packageName}/$versionName"
    }

    @Provides
    @CoreScope
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        @InternalApi
        cache: Cache,
        @InternalApi
        userAgent: String
    ): OkHttpClient = OkHttpClient
        .Builder()
        .connectTimeout(DEFAULT_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
        .readTimeout(READ_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
        .writeTimeout(WRITE_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
        .cache(cache)
        .addInterceptor { chain ->
            chain.proceed(
                chain.request()
                    .newBuilder()
                    .header("User-Agent", userAgent)
                    .build()
            )
        }
        .addInterceptor(httpLoggingInterceptor)
        .build()

    /**
     *  Every API client shares this, so they all go through the
     *  same OkHttp cache, user agent and logging.
     */
    @Provides
    @CoreScope
    fun provideHttpClient(okHttpClient: OkHttpClient): HttpClient {
        assertNoInitMainThread()
        return HttpClient(OkHttp) {
            engine {
                preconfigured = okHttpClient
            }
            install(ContentNegotiation) {
                json(defaultJson)
            }
        }
    }
}