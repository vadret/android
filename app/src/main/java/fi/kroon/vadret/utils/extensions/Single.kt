package fi.kroon.vadret.utils.extensions

import io.reactivex.Single

fun <T> T.asSingle(): Single<T> = Single.just(this)