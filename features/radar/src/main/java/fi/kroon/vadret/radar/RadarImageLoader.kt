package fi.kroon.vadret.radar

import android.content.Context
import android.graphics.Bitmap
import coil3.ImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.ImageResult
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap
import dagger.Lazy
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.radar.di.RadarScope
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import io.ktor.client.HttpClient
import timber.log.Timber
import javax.inject.Inject

/**
 *  Loads radar images through the shared Ktor client, so they
 *  use the same cache, user agent and logging as the API calls.
 *  The client is created on first use, never on the main thread.
 */
@RadarScope
class RadarImageLoader @Inject constructor(
    private val context: Context,
    httpClient: Lazy<HttpClient>
) {

    private val imageLoader: ImageLoader = ImageLoader.Builder(context = context)
        .components {
            add(KtorNetworkFetcherFactory(httpClient = { httpClient.get() }))
        }
        .build()

    /**
     *  Software bitmaps, osmdroid draws them on its own canvas.
     */
    suspend fun load(url: String): Either<Failure, Bitmap> {
        val request: ImageRequest = ImageRequest.Builder(context = context)
            .data(data = url)
            .allowHardware(enable = false)
            .build()
        return when (val result: ImageResult = imageLoader.execute(request = request)) {
            is SuccessResult -> result.image.toBitmap().asRight()
            is ErrorResult -> {
                Timber.e(result.throwable, "Failed loading radar image $url")
                Failure.IOException.asLeft()
            }
        }
    }
}