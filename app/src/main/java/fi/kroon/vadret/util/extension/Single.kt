package fi.kroon.vadret.util.extension

import io.reactivex.Single

fun <T> T.asSingle(): Single<T> = Single.just(this)