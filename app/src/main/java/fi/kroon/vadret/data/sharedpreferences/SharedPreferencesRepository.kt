package fi.kroon.vadret.data.sharedpreferences

import android.content.SharedPreferences
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.di.scope.VadretApplicationScope
import io.reactivex.Single
import javax.inject.Inject

@VadretApplicationScope
class SharedPreferencesRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    fun putString(key: String, value: String): Single<Either<Failure, Unit>> = Single.fromCallable {
        with(sharedPreferences.edit()) {
            putString(key, value).apply()
        }
        Either.Right(Unit)
    }

    fun putBoolean(key: String, value: Boolean): Single<Either<Failure, Unit>> = Single.fromCallable {
        with(sharedPreferences.edit()) {
            putBoolean(key, value).apply()
        }
        Either.Right(Unit)
    }

    fun getString(key: String): Single<Either<Failure, String>> = Single.fromCallable {
        sharedPreferences.getString(key, null)
    }.map {
        Either.Right(it)
    }

    fun getBoolean(key: String): Single<Either<Failure, Boolean>> = Single.fromCallable {
        sharedPreferences.getBoolean(key, false)
    }.map {
        Either.Right(it)
    }
}