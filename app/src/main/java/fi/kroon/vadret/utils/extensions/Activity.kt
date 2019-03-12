package fi.kroon.vadret.utils.extensions

import android.app.Activity
import fi.kroon.vadret.BaseApplication
import fi.kroon.vadret.core.di.component.VadretApplicationComponent

fun Activity.appComponent(): VadretApplicationComponent =
    BaseApplication
        .appComponent(this)