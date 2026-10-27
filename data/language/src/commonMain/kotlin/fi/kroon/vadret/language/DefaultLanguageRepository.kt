package fi.kroon.vadret.language

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.common.catch
import fi.kroon.vadret.common.either
import fi.kroon.vadret.language.extension.toAppLanguage
import fi.kroon.vadret.language.extension.toFailure
import fi.kroon.vadret.language.extension.toPreferenceValue
import fi.kroon.vadret.language.local.LanguageLocalDataSource
import fi.kroon.vadret.language.model.AppLanguage
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asLeft
import io.github.sphrak.either.extension.asRight
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

internal class DefaultLanguageRepository(
    private val languageLocalDataSource: LanguageLocalDataSource
) : LanguageRepository {

    override suspend fun getLanguage(): Either<Failure, AppLanguage> =
        either { languageLocalDataSource.getLanguage().toAppLanguage() }
            .catch { exception: Exception -> exception.toFailure() }

    override fun observeLanguage(): Flow<Either<Failure, AppLanguage>> =
        languageLocalDataSource
            .observeLanguage()
            .map<String?, Either<Failure, AppLanguage>> { value: String? -> value.toAppLanguage().asRight() }
            .catch { throwable: Throwable ->
                if (throwable is Exception) {
                    emit(throwable.toFailure().asLeft())
                } else {
                    throw throwable
                }
            }

    override suspend fun setLanguage(language: AppLanguage): Either<Failure, Unit> =
        either { languageLocalDataSource.setLanguage(value = language.toPreferenceValue()) }
            .catch { exception: Exception -> exception.toFailure() }
}