package fi.kroon.vadret.di.modules

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.di.scope.VadretApplicationScope
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

@Module(includes = [ApplicationModule::class])
object NetworkModule {

    private const val DEFAULT_CONNECTION_TIMEOUT = 10000L
    private const val READ_CONNECTION_TIMEOUT = 10000L
    private const val WRITE_CONNECTION_TIMEOUT = 10000L

    @Provides
    @JvmStatic
    @VadretApplicationScope
    fun httpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    @JvmStatic
    @VadretApplicationScope
    fun okHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(DEFAULT_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
        .readTimeout(READ_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
        .writeTimeout(WRITE_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
        .addInterceptor(httpLoggingInterceptor)
        .build()
}