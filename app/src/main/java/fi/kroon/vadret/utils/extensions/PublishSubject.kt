package fi.kroon.vadret.utils.extensions

import io.reactivex.BackpressureStrategy
import io.reactivex.subjects.PublishSubject

fun <T> PublishSubject<T>.toObservable() =
    toFlowable(BackpressureStrategy.BUFFER)
        .toObservable()