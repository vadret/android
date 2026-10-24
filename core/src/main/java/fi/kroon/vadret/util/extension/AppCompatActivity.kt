package fi.kroon.vadret.util.extension

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import fi.kroon.vadret.core.CoreComponent
import fi.kroon.vadret.core.CoreComponentProvider

val AppCompatActivity.coreComponent: CoreComponent
    get(): CoreComponent = (applicationContext as CoreComponentProvider).coreComponent

val Fragment.coreComponent: CoreComponent
    get(): CoreComponent = (requireContext().applicationContext as CoreComponentProvider).coreComponent