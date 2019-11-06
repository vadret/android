package fi.kroon.vadret.util.extension

import dagger.Lazy
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import timber.log.Timber

@PublishedApi
internal inline fun Retrofit.Builder.callFactory(
    crossinline body: (Request) -> Call
): Retrofit.Builder = callFactory(
    object : Call.Factory {
        override fun newCall(request: Request): Call = body(request)
    }
)

@Suppress("NOTHING_TO_INLINE")
inline fun Retrofit.Builder.delegatingCallFactory(
    delegate: Lazy<OkHttpClient>
): Retrofit.Builder = callFactory {
    Timber.d("Displayed fi.kroon.vadret/.presentation.main.MainActivity: thread ${Thread.currentThread()}")
    delegate.get().newCall(it)
}