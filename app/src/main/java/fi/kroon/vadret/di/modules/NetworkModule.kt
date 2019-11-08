package fi.kroon.vadret.di.modules

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.BuildConfig
import fi.kroon.vadret.di.scope.CoreApplicationScope
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

@Module(
    includes = [
        ContextModule::class
    ]
)
object NetworkModule {

    private const val DEFAULT_CONNECTION_TIMEOUT = 10000L
    private const val READ_CONNECTION_TIMEOUT = 10000L
    private const val WRITE_CONNECTION_TIMEOUT = 10000L

    private val getLogLevel: HttpLoggingInterceptor.Level =
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }

    @Provides
    @JvmStatic
    @CoreApplicationScope
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor()
            .apply { this.level = getLogLevel }

    @Provides
    @JvmStatic
    @CoreApplicationScope
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(DEFAULT_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
        .readTimeout(READ_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
        .writeTimeout(WRITE_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
        .addInterceptor(httpLoggingInterceptor)
        .build()
}