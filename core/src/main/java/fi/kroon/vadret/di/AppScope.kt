package fi.kroon.vadret.di

import javax.inject.Scope

/**
 *  One instance for the whole process.
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class AppScope