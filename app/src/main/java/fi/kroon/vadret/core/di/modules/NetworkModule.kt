package fi.kroon.vadret.core.di.modules

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.core.di.scope.VadretApplicationScope
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

    @Provides
    @JvmStatic
    @VadretApplicationScope
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    @JvmStatic
    @VadretApplicationScope
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(DEFAULT_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
        .readTimeout(READ_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
        .writeTimeout(WRITE_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
        .addInterceptor(httpLoggingInterceptor)
        .build()
}