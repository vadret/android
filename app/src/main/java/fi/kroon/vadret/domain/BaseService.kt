package fi.kroon.vadret.domain

abstract class BaseService {

    /**
     *  Look at me, I am the API now
     */
    val currentTimeMillis: Long
        get() = System.currentTimeMillis()
}