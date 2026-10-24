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
import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.logging.Logger
import fi.kroon.vadret.logging.LoggerFactory
import fi.kroon.vadret.radar.di.RadarScope
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asLeft
import io.github.sphrak.either.extension.asRight
import io.ktor.client.HttpClient
import javax.inject.Inject

private val logger: Logger = LoggerFactory.getLogger(tag = "fi.kroon.vadret.radar")

/**
 *  Coil reports failures as results rather than exceptions. The
 *  client is created on first use, never on the main thread.
 */
@RadarScope
internal class DefaultRadarImageLoader @Inject constructor(
    private val context: Context,
    httpClient: Lazy<HttpClient>
) : RadarImageLoader {

    private val imageLoader: ImageLoader = ImageLoader.Builder(context = context)
        .components {
            add(KtorNetworkFetcherFactory(httpClient = { httpClient.get() }))
        }
        .build()

    override suspend fun load(imageUrl: String): Either<Failure, Bitmap> {
        val request: ImageRequest = ImageRequest.Builder(context = context)
            .data(data = imageUrl)
            .allowHardware(enable = false)
            .build()
        return when (val result: ImageResult = imageLoader.execute(request = request)) {
            is SuccessResult -> result.image.toBitmap().asRight()
            is ErrorResult -> {
                logger.warn { "${ErrorCode.NETWORK_FAILURE}: radar image $imageUrl: ${result.throwable.message}" }
                Failure(errorCode = ErrorCode.NETWORK_FAILURE).asLeft()
            }
        }
    }
}