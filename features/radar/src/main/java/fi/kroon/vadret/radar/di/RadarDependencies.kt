package fi.kroon.vadret.radar.di

import android.content.Context
import fi.kroon.vadret.radar.RadarRepository
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher

/**
 *  What the radar screen needs from the app. The images are
 *  loaded through the same [HttpClient] as every API call.
 */
interface RadarDependencies {
    fun radarRepository(): RadarRepository
    fun httpClient(): HttpClient
    fun context(): Context
    fun coroutineDispatcher(): CoroutineDispatcher
}