package fi.kroon.vadret.util.extension

import android.content.Context
import fi.kroon.vadret.core.CoreComponent
import fi.kroon.vadret.core.CoreComponentProvider

val Context.coreComponent: CoreComponent
    get() = (applicationContext as CoreComponentProvider).coreComponent