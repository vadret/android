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
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.radar.extension.toFailure
import fi.kroon.vadret.radar.projection.RadarReprojection
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asLeft
import io.github.sphrak.either.extension.asRight
import io.ktor.client.HttpClient

internal class DefaultRadarImageLoader(
    private val context: Context,
    radarReprojection: RadarReprojection,
    httpClient: Lazy<HttpClient>
) : RadarImageLoader {

    private val imageLoader: ImageLoader = ImageLoader.Builder(context = context)
        .components {
            add(KtorNetworkFetcherFactory(httpClient = { httpClient.value }))
            add(TiffRadarDecoderFactory(radarReprojection = radarReprojection))
        }
        .build()

    override suspend fun load(imageUrl: String): Either<Failure, Bitmap> {
        val request: ImageRequest = ImageRequest.Builder(context = context)
            .data(data = imageUrl)
            .allowHardware(enable = false)
            .build()
        return when (val result: ImageResult = imageLoader.execute(request = request)) {
            is SuccessResult -> result.image.toBitmap().asRight()
            is ErrorResult -> result.throwable.toFailure().asLeft()
        }
    }
}