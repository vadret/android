package fi.kroon.vadret.domain

interface IService {

    /**
     *  Look at me, I am the API now
     */
    val currentTimeMillis: Long
        get() = System.currentTimeMillis()
}