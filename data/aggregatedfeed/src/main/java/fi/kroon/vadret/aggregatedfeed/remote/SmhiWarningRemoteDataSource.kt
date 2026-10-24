package fi.kroon.vadret.aggregatedfeed.remote

internal interface SmhiWarningRemoteDataSource {
    suspend fun getWarnings(): List<SmhiWarningDto>
}