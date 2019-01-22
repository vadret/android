package fi.kroon.vadret.utils.extensions

import io.reactivex.Observable

fun <T> T.asObservable(): Observable<T> = Observable.just(this)