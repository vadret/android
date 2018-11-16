package fi.kroon.vadret.data.alert.adapter

import com.squareup.moshi.JsonQualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

/**
 *  Annotate JSON fields
 *  where sometimes object
 *  is accessible directly
 *  rather than in a List::class
 */
@Retention(RUNTIME)
@JsonQualifier
annotation class SingleToArray