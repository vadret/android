package fi.kroon.vadret.util.extension

import android.os.Looper

fun assertNoInitMainThread(): Unit = if (Looper.myLooper() == Looper.getMainLooper()) {
    throw IllegalStateException("illegal instantiation on main thread.")
} else Unit