package fi.kroon.vadret.autocomplete.local

internal interface AutoCompleteLocalDataSource {
    suspend fun search(
        prefix: String,
        limit: Int
    ): List<AutoCompleteItemEntity>
}