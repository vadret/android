package fi.kroon.vadret.util.extension

import android.app.Service
import fi.kroon.vadret.BaseApplication
import fi.kroon.vadret.di.component.CoreApplicationComponent

fun Service.appComponent(): CoreApplicationComponent =
    BaseApplication
        .appComponent(this)