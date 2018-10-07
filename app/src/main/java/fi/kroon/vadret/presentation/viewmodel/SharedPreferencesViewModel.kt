package fi.kroon.vadret.presentation.viewmodel

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.domain.SharedPreferencesUseCase
import io.reactivex.Single
import javax.inject.Inject

class SharedPreferencesViewModel @Inject constructor(
    private val sharedPreferencesUseCase: SharedPreferencesUseCase
) : BaseViewModel() {
    fun putString(key: String, value: String): Single<Either<Failure, Unit>> = sharedPreferencesUseCase.putString(key, value)
    fun putBoolean(key: String, value: Boolean): Single<Either<Failure, Unit>> = sharedPreferencesUseCase.putBoolean(key, value)
    fun getString(key: String): Single<Either<Failure, String>> = sharedPreferencesUseCase.getString(key)
    fun getBoolean(key: String): Single<Either<Failure, Boolean>> = sharedPreferencesUseCase.getBoolean(key)
}