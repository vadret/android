package fi.kroon.vadret.aggregatedfeed

import fi.kroon.vadret.aggregatedfeed.local.SeenWarningEntity
import fi.kroon.vadret.aggregatedfeed.local.SeenWarningLocalDataSource
import fi.kroon.vadret.aggregatedfeed.remote.KrisinformationFeedDto
import fi.kroon.vadret.aggregatedfeed.remote.KrisinformationRemoteDataSource
import fi.kroon.vadret.aggregatedfeed.remote.SmhiWarningDto
import fi.kroon.vadret.aggregatedfeed.remote.SmhiWarningRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

internal class FakeSmhiWarningRemoteDataSource(
    var smhiWarningDtos: List<SmhiWarningDto> = emptyList()
) : SmhiWarningRemoteDataSource {

    var requestCount: Int = 0

    override suspend fun getWarnings(): List<SmhiWarningDto> {
        requestCount++
        return smhiWarningDtos
    }
}

internal class FakeKrisinformationRemoteDataSource(
    var news: () -> List<KrisinformationFeedDto> = { emptyList() },
    var publicAlerts: () -> List<KrisinformationFeedDto> = { emptyList() }
) : KrisinformationRemoteDataSource {

    override suspend fun getNews(): List<KrisinformationFeedDto> = news()

    override suspend fun getPublicAlerts(): List<KrisinformationFeedDto> = publicAlerts()
}

internal class EmptySeenWarningLocalDataSource : SeenWarningLocalDataSource {
    override fun observeSeenWarnings(): Flow<List<SeenWarningEntity>> = flowOf(emptyList())
    override suspend fun markSeen(seenWarningEntity: SeenWarningEntity) = Unit
}

internal class FailingSeenWarningLocalDataSource(
    private val exception: Exception
) : SeenWarningLocalDataSource {
    override fun observeSeenWarnings(): Flow<List<SeenWarningEntity>> = flow { throw exception }
    override suspend fun markSeen(seenWarningEntity: SeenWarningEntity) = throw exception
}