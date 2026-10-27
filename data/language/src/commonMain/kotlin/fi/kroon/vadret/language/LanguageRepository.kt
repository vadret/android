package fi.kroon.vadret.language

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.language.local.DefaultLanguageLocalDataSource
import fi.kroon.vadret.language.model.AppLanguage
import io.github.sphrak.either.Either
import kotlinx.coroutines.flow.Flow

interface LanguageRepository {
    suspend fun getLanguage(): Either<Failure, AppLanguage>
    fun observeLanguage(): Flow<Either<Failure, AppLanguage>>
    suspend fun setLanguage(language: AppLanguage): Either<Failure, Unit>
}

fun LanguageRepository(dataStore: DataStore<Preferences>): LanguageRepository =
    DefaultLanguageRepository(
        languageLocalDataSource = DefaultLanguageLocalDataSource(dataStore = dataStore)
    )