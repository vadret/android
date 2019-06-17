package fi.kroon.vadret.util.extension

import io.reactivex.Observable

fun <T> T.asObservable(): Observable<T> = Observable.just(this)