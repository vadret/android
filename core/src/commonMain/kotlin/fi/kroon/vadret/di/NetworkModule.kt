package fi.kroon.vadret.di

import fi.kroon.vadret.common.defaultJson
import fi.kroon.vadret.model.AppInfo
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import org.koin.core.module.Module
import org.koin.dsl.module

val networkModule: Module = module {
    single<HttpClient> {
        httpClient(
            httpClientEngine = get(),
            appInfo = get()
        )
    }
}

private fun httpClient(
    httpClientEngine: HttpClientEngine,
    appInfo: AppInfo
): HttpClient =
    HttpClient(engine = httpClientEngine) {
        install(plugin = UserAgent) {
            agent = "${appInfo.applicationId}/${appInfo.versionName}"
        }
        install(plugin = ContentNegotiation) {
            json(json = defaultJson)
        }
    }