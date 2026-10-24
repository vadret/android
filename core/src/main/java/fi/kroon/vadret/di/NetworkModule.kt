package fi.kroon.vadret.di

import android.content.Context
import android.os.Looper
import fi.kroon.vadret.common.defaultJson
import fi.kroon.vadret.core.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File
import java.util.concurrent.TimeUnit

private const val CONNECTION_TIMEOUT_MILLIS = 10_000L
private const val READ_TIMEOUT_MILLIS = 10_000L
private const val WRITE_TIMEOUT_MILLIS = 10_000L
private const val CACHE_SIZE_BYTES: Long = 33_554_432L
private const val CACHE_DIRECTORY = "okhttp_cache"
private const val USER_AGENT_HEADER = "User-Agent"

val networkModule: Module = module {
    single<HttpClient> { httpClient(context = get()) }
}

private fun httpClient(context: Context): HttpClient {
    check(Looper.myLooper() != Looper.getMainLooper()) { "The HTTP client must not be built on the main thread" }
    return HttpClient(OkHttp) {
        engine {
            preconfigured = okHttpClient(context = context)
        }
        install(ContentNegotiation) {
            json(json = defaultJson)
        }
    }
}

private fun okHttpClient(context: Context): OkHttpClient {
    val userAgent: String = userAgent(context = context)
    return OkHttpClient
        .Builder()
        .connectTimeout(CONNECTION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
        .readTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
        .writeTimeout(WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
        .cache(
            Cache(
                directory = File(context.cacheDir, CACHE_DIRECTORY),
                maxSize = CACHE_SIZE_BYTES
            )
        )
        .addInterceptor { chain ->
            chain.proceed(
                chain.request()
                    .newBuilder()
                    .header(USER_AGENT_HEADER, userAgent)
                    .build()
            )
        }
        .addInterceptor(httpLoggingInterceptor())
        .build()
}

private fun userAgent(context: Context): String {
    val versionName: String? = context.packageManager
        .getPackageInfo(context.packageName, 0)
        .versionName
    return "${context.packageName}/$versionName"
}

private fun httpLoggingInterceptor(): HttpLoggingInterceptor =
    HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }