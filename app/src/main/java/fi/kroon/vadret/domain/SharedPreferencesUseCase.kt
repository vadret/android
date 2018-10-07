package fi.kroon.vadret.domain

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.sharedpreferences.SharedPreferencesRepository
import io.reactivex.Single
import javax.inject.Inject

class SharedPreferencesUseCase @Inject constructor(
    private val sharedPreferencesRepository: SharedPreferencesRepository
) {
    fun putString(key: String, value: String): Single<Either<Failure, Unit>> = sharedPreferencesRepository.putString(key, value)
    fun putBoolean(key: String, value: Boolean): Single<Either<Failure, Unit>> = sharedPreferencesRepository.putBoolean(key, value)
    fun getString(key: String): Single<Either<Failure, String>> = sharedPreferencesRepository.getString(key)
    fun getBoolean(key: String): Single<Either<Failure, Boolean>> = sharedPreferencesRepository.getBoolean(key)
}