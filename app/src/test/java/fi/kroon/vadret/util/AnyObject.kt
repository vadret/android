package fi.kroon.vadret.util

import org.mockito.Mockito

fun <T> anyObject(): T {
    Mockito.anyObject<T>()
    return uninitialized()
}

fun <T> uninitialized(): T = null as T