package fi.kroon.vadret.data.alert.adapter

import com.squareup.moshi.JsonQualifier
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FIELD

/**
 *  Annotate JSON fields
 *  where sometimes object
 *  is accessible directly
 *  rather than in a List::class
 */
@Target(FIELD)
@Retention(RUNTIME)
@JsonQualifier
annotation class SingleToArray