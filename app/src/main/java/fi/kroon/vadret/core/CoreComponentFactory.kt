package fi.kroon.vadret.core

import android.content.Context

object CoreComponentFactory {

    @Volatile
    private var instance: CoreComponent? = null

    fun getInstance(context: Context): CoreComponent =
        instance ?: synchronized(this) {
            instance ?: DaggerCoreComponent
                .factory()
                .create(
                    context = context
                ).also { instance = it }
        }
}