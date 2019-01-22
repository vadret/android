package fi.kroon.vadret.utils.extensions

import android.app.Activity
import fi.kroon.vadret.BaseApplication

fun Activity.appComponent() = BaseApplication.appComponent(this)