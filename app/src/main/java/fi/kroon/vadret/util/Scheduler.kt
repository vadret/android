package fi.kroon.vadret.util

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class Schedulers {
    fun single(): Scheduler = Schedulers.single()
    fun io(): Scheduler = Schedulers.io()
    fun ui(): Scheduler = AndroidSchedulers.mainThread()
    fun computation(): Scheduler = Schedulers.computation()
}