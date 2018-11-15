package fi.kroon.vadret

import android.content.Context

class VadretApplication : BaseApplication() {

    companion object {
        operator fun get(context: Context): VadretApplication {
            return context.applicationContext as VadretApplication
        }
    }
}